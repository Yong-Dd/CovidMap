package com.yongdd.covid_map.model.repository

import com.yongdd.covid_map.api.service.CenterService
import com.yongdd.covid_map.model.data.Center
import com.yongdd.covid_map.database.CenterDao
import javax.inject.Inject

class CenterRepository @Inject constructor(
    private val centerDao: CenterDao,
    private val centerService: CenterService
) {
    // -----[api]-----

    // 전체 리스트 가져오기 (perPage:개수, 기본 개수 10개)
    suspend fun getCenterListFromAPI(page: Int, perPage: Int?) =
        centerService.centerList(page, perPage?:10)

    // -----[dao]-----

    // 전체 리스트
    fun getCenterListFromDao() : List<Center> = centerDao.getAllCenters()

    // db에 추가
    fun addCenterDao(center: Center){
        centerDao.addCenter(center)
    }

    // db 수정
    fun updateDao(center: Center) {
        centerDao.updateCenter(center)
    }

    // db 현재 개수
    fun getAllCountDao() : Int = centerDao.getAllCount()

}