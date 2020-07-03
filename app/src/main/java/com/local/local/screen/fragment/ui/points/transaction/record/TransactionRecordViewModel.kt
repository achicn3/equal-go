package com.local.local.screen.fragment.ui.points.transaction.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.TransactionInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil
import java.util.*

class TransactionRecordViewModel : ViewModel() {
    private val calendarImp = MutableLiveData(Calendar.getInstance(Locale.TAIWAN))
    val calendar: LiveData<Calendar> = calendarImp
    val record = MutableLiveData(mutableListOf<TransactionInfo>())
    private val firebaseCallback = object : FirebaseCallback() {
        override fun userRetrieveTransactionRecord(transactionInfo: List<TransactionInfo>) {
            record.value?.clear()
            record.value?.addAll(transactionInfo)
            Log.d("status","in retreieve record : $record")
            record.notifyObserver()
        }
    }

    init {
        calendarImp.value = Calendar.getInstance(Locale.TAIWAN)
    }

    fun incrementDay() {
        calendarImp.value?.add(Calendar.DATE, 1)
        calendarImp.notifyObserver()
    }

    fun decrementDay() {
        calendarImp.value?.add(Calendar.DATE, -1)
        calendarImp.notifyObserver()
    }

    fun setDate(year: Int, month: Int, day: Int) {
        calendarImp.value?.set(year, month, day)
        calendarImp.notifyObserver()
    }

    fun retrieveRecord(year: String, month: String, day: String) {
        FirebaseUtil.userRetrieveTransactionInfo(year, month, day, firebaseCallback)
    }
}