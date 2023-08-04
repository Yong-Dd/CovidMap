package com.yongdd.covid_map.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yongdd.covid_map.model.data.Center

@Dao
interface CenterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCenter(center: Center)

    @Update
    fun updateCenter(center: Center)

    @Query("SELECT * FROM Centers")
    fun getAllCenters() : List<Center>

    @Query("SELECT COUNT(*) FROM Centers")
    fun getAllCount() : Int

}