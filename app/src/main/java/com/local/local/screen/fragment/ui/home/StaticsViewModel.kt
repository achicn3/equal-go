package com.local.local.screen.fragment.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.RecordInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil
import java.util.*
import kotlin.collections.ArrayList

class StaticsViewModel : ViewModel() {
    private val calendarImp = MutableLiveData<Calendar>()
    val calendar : LiveData<Calendar>
        get() = calendarImp

    private val allMonthRecordInfoImp = MutableLiveData(mutableListOf<RecordInfo?>())
    val allMonthRecordInfo : LiveData<List<RecordInfo?>>
        get() = MutableLiveData(allMonthRecordInfoImp.value?.toList())

    private val firebaseCallback = object : FirebaseCallback(){
        override fun retrieveStatics(recordInfoList: List<RecordInfo?>) {
            super.retrieveStatics(recordInfoList)
            allMonthRecordInfoImp.value?.addAll(recordInfoList)
            Log.d("status","fetch statics : $recordInfoList")
            allMonthRecordInfoImp.notifyObserver()
        }
    }


    init{
        calendarImp.value = Calendar.getInstance(Locale.TAIWAN)
    }

    fun incrementMonth(){
        calendarImp.value?.add(Calendar.MONTH,1)
        calendarImp.notifyObserver()
    }

    fun decrementMonth(){
        calendarImp.value?.add(Calendar.MONTH,-1)
        calendarImp.notifyObserver()
    }

    fun setDate(year: Int,month: Int){
        calendarImp.value?.set(year,month,0)
        calendarImp.notifyObserver()
    }

    fun searchRecord(year: Int,month: Int){
        FirebaseUtil.retrieveStatics(year,month,firebaseCallback)
    }
}