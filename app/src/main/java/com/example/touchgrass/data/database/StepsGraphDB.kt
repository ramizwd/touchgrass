package com.example.touchgrass.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StepsGraph::class], version = 1)
abstract class StepsGraphDB: RoomDatabase() {
    abstract fun stepsGraphDao(): StepsGraphDao

    companion object {
        private var instance: StepsGraphDB? = null
        @Synchronized
        fun get(context: Context): StepsGraphDB {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(context.applicationContext,
                    StepsGraphDB::class.java, "stepsGraph.db").build()
            }
            return instance!!
        }
    }
}