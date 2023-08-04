package com.yongdd.covid_map.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yongdd.covid_map.model.data.Center

@Database(entities = [Center::class], version = 1, exportSchema = false)
abstract class CenterDatabase : RoomDatabase() {
    abstract fun centerDao() : CenterDao

    companion object {
        const val DATABASE_NAME = "covidCenter.db"
    }
}