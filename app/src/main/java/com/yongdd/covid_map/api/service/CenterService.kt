package com.yongdd.covid_map.api.service

import com.yongdd.covid_map.model.data.CenterRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CenterService {
    // 예방접종센터 위치정보 API
    @GET("15077586/v1/centers")
    suspend fun centerList(
        @Query("page") page: Int,
        @Query("perPage") size: Int
    ): Response<CenterRes>
}