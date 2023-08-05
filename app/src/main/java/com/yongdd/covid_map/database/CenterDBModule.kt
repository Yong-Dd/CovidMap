package com.yongdd.covid_map.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CenterDBModule {
    @Singleton
    @Provides
    fun provideCenterDatabase(@ApplicationContext context: Context) : CenterDatabase {
        return Room
            .databaseBuilder(
                context,
                CenterDatabase::class.java,
                CenterDatabase.DATABASE_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun provideCenterDAO(centerDB: CenterDatabase): CenterDao {
        return centerDB.centerDao()
    }
}