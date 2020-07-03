package com.local.local.screen.store.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.RecordInfo
import com.local.local.body.StoreTransactionRecordBody
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.manager.StoreLoginManager
import com.local.local.util.FirebaseUtil
import java.util.*

class StoreRecordViewModel : ViewModel() {
    private val calendarImp = MutableLiveData<Calendar>()
    val calendar : LiveData<Calendar>
        get() = calendarImp

    private val allMonthRecordInfoImp = MutableLiveData(mutableListOf<StoreTransactionRecordBody>())
    val allMonthRecordInfo : LiveData<MutableList<StoreTransactionRecordBody>>
        get() = allMonthRecordInfoImp


    private val firebaseCallback = object : FirebaseCallback(){
        override fun storeRetrieveTransactionRecord(transactionInfo: List<StoreTransactionRecordBody>) {
            allMonthRecordInfoImp.value?.clear()
            allMonthRecordInfoImp.value?.addAll(transactionInfo)
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

    fun searchRecord(year: String,month: String){
        val storeInfo = StoreLoginManager.instance.storeInfo ?: return
        FirebaseUtil.storeRetrieveTransactionInfo(storeInfo,year,month,firebaseCallback)
    }
}