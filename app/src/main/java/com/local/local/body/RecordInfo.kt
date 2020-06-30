package com.local.local.body

/**
 * 當下累計的點數、距離、跟在幾號累積的
 * */
data class RecordInfo(
        var distance: Float = 0f,
        var points: Int = 0,
        var days : Int? = null
)