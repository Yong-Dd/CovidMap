package com.yongdd.covid_map.model.data

data class CenterRes(
    val currentCount: Int,
    val `data`: List<CenterInfo>,
    val matchCount: Int,
    val page: Int,
    val perPage: Int,
    val totalCount: Int
)