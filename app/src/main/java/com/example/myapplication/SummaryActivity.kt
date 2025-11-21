package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SummaryActivity : AppCompatActivity() {
    private lateinit var db: ActivityDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ActivityDB.getInstance(this)

        val date = intent.getStringExtra("date") ?: ""
        val summaryTv = findViewById<TextView>(R.id.summary_tv)

        lifecycleScope.launch(Dispatchers.IO) {
            val total = db.activityDao().getTotalDurationByDate(date) ?: 0
            withContext(Dispatchers.Main){
                summaryTv.text = "Date: $date\nTotal Duration: $total minutes"

            }
        }
    }
}
