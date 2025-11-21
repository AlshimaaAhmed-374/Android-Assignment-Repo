package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {

    @Insert
    suspend fun insertActivity(activity: Activity)

    @Query("SELECT * FROM activities")
    suspend fun getAllActivities(): List<Activity>

    @Query("SELECT * FROM activities WHERE Act_Date = :selectedDate")
    suspend fun getActivitiesByDate(selectedDate: String): List<Activity>

    @Query("SELECT SUM(Act_Dur) FROM activities WHERE Act_Date = :selectedDate")
    suspend fun getTotalDurationByDate(selectedDate: String): Int?
}
