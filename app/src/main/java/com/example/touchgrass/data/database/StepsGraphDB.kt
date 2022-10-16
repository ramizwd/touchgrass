package com.example.touchgrass.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database that stores [StepsGraph]'s information with [get] function for accessing the database,
 * an abstract function [stepsGraphDao] for connecting the database to [StepsGraphDao] DAO,
 * and a variable [instance] for keeping a reference to the database.
 */
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