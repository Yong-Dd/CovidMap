package com.yongdd.covid_map.model.repository

import com.yongdd.covid_map.api.service.CenterService
import com.yongdd.covid_map.database.CenterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideCenterRepository(centerDao: CenterDao, centerService: CenterService) =
        CenterRepository(centerDao, centerService)
}