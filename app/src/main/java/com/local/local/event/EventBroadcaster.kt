package com.local.local.event

import androidx.lifecycle.LiveData

interface EventBroadcaster<T> {
    val eventLiveData: LiveData<T>
    fun onEventConsumed(event: T?)
}