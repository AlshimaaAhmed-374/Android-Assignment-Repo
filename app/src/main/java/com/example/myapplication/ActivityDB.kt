package com.example.myapplication

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Activity::class], version = 1)
abstract class ActivityDB : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var instance: ActivityDB? = null

        fun getInstance(context: Context): ActivityDB {
            if (instance == null) {
                synchronized(ActivityDB) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ActivityDB::class.java,
                        "activity_db"
                    ).build()
                }
            }
            return instance!!
        }
    }
}
