package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var db: ActivityDB
    private lateinit var listView: ListView

    private lateinit var etName: EditText
    private lateinit var etDuration: EditText
    private lateinit var calendarView: CalendarView
    private lateinit var btnAdd: Button
    private lateinit var btnFilter: Button
    private lateinit var btnShowAll: Button

    private var selectedDate = ""
    private lateinit var currentList: MutableList<Activity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = ActivityDB.getInstance(this)


        etName = findViewById(R.id.name_et)
        etDuration = findViewById(R.id.duration_et)
        calendarView = findViewById(R.id.calender_view)
        btnAdd = findViewById(R.id.add_btn)
        btnFilter = findViewById(R.id.filter_btn)
        btnShowAll = findViewById(R.id.showall_btn)
        listView = findViewById(R.id.lst_view)

        currentList = mutableListOf()

        calendarView.setOnDateChangeListener { _, year, month, day ->
            selectedDate = "$day/${month + 1}/$year"
        }

        btnAdd.setOnClickListener {
            addActivity()
        }

        btnShowAll.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                currentList = db.activityDao().getAllActivities().toMutableList()

                withContext(Dispatchers.Main) {

                    val adapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        currentList.map { "${it.Act_Name} - ${it.Act_Dur} min - (${it.Act_Date})" }
                    )
                    listView.adapter = adapter
                }
            }
        }

        btnFilter.setOnClickListener {

            if (selectedDate.isNotEmpty()) {

                lifecycleScope.launch(Dispatchers.IO) {
                    currentList = db.activityDao().getActivitiesByDate(selectedDate).toMutableList()

                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_list_item_1,
                            currentList.map {
                                "${it.Act_Name} - ${it.Act_Dur} min - ${it.Act_Date}"
                            }
                        )
                        listView.adapter = adapter
                    }

                }

            } else {
                Toast.makeText(this, "Select a date first", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = currentList[position]

            val intent = Intent(this, SummaryActivity::class.java)
            intent.putExtra("date", clickedItem.Act_Date)
            startActivity(intent)
        }

    }
    private fun addActivity() {
        
        val name = etName.text.toString()
        val duration = etDuration.text.toString()

        if (name.isNotEmpty() && duration.isNotEmpty() && selectedDate.isNotEmpty()) {

            val my_activity = Activity(
                Act_Name = name,
                Act_Dur = duration.toInt(),
                Act_Date = selectedDate
            )

            lifecycleScope.launch(Dispatchers.IO) {
                db.activityDao().insertActivity(my_activity)

                withContext(Dispatchers.Main) {
                    currentList.add(my_activity)

                    val adapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        currentList.map { "${it.Act_Name} - ${it.Act_Dur} min - (${it.Act_Date})" }
                    )
                    listView.adapter = adapter

                    Toast.makeText(this@MainActivity, "Activity Saved!", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

}

