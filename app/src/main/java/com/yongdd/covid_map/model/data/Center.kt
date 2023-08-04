package com.yongdd.covid_map.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room에서 사용할 data class
@Entity(tableName = "Centers")
data class Center(
    @PrimaryKey
    var id: Int,

    var address: String,
    var centerName: String,
    var centerType: String,
    var createdAt: String,
    var facilityName: String,
    var lat: String,
    var lng: String,
    var org: String,
    var phoneNumber: String,
    var sido: String,
    var sigungu: String,
    var updatedAt: String,
    var zipCode: String
)
