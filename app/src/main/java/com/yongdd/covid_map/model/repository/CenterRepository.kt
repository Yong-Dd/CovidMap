package com.yongdd.covid_map.model.repository

import com.yongdd.covid_map.api.service.CenterService
import com.yongdd.covid_map.model.data.Center
import com.yongdd.covid_map.database.CenterDao
import com.yongdd.covid_map.model.data.CenterRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import retrofit2.Response
import javax.inject.Inject
class CenterRepository @Inject constructor(
    private val centerDao: CenterDao,
    private val centerService: CenterService
) {
    // -----[api]-----

    // 전체 리스트 가져오기 (perPage:개수, 기본 개수 10개)
    //suspend fun getCenterListFromAPI(page: Int, perPage: Int?) =
    //    centerService.centerList(page, perPage ?: 10)
    suspend fun getCenterListFromAPI(page: Int, perPage: Int?) =
        flow {
            val response = centerService.centerList(page, perPage ?: 10)
            emit(response)
        }.flowOn(Dispatchers.IO)


    // -----[dao]-----

    // 전체 리스트
    val centerListFromDao : Flow<List<Center>> = flow {
        val centerList = centerDao.getAllCenters()
        emit(centerList)
    }.flowOn(Dispatchers.IO)

    // db에 추가
    fun addCenterDao(center: Center) {
        centerDao.addCenter(center)
    }

    // db 수정
    fun updateDao(center: Center) {
        centerDao.updateCenter(center)
    }

    // db 현재 개수
    val centerListTotalCount : Flow<Int> = flow {
        val totalCount = centerDao.getTotalCount()
        emit(totalCount)
    }.flowOn(Dispatchers.IO)

}

