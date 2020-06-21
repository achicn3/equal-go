package com.kdanmobile.cloud.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ConcurrentLinkedQueue

class EventManager<T> : EventBroadcaster<T> {
    private val queue = ConcurrentLinkedQueue<T>()
    private val eventLiveDataImp = MutableLiveData<T>()
    override val eventLiveData: LiveData<T> = eventLiveDataImp
    private var currentEvent: T? = null

    @Synchronized
    fun send(event: T) {
        if (currentEvent == null) {
            currentEvent = event
            eventLiveDataImp.postValue(currentEvent)
        } else {
            queue.offer(event)
        }
    }

    @Synchronized
    override fun onEventConsumed(event: T?) {
        if (event == null) return
        if (event != currentEvent) return
        currentEvent = queue.poll()
        if (currentEvent != null || eventLiveDataImp.value != null) {
            eventLiveDataImp.postValue(currentEvent)
        }
    }
}