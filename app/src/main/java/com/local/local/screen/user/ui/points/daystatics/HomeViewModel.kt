package com.local.local.screen.user.ui.points.daystatics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.RecordInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil
import java.util.*

class HomeViewModel : ViewModel() {
    private val calendarImp = MutableLiveData<Calendar>()
    val calendar : LiveData<Calendar>
        get() = calendarImp

    private val recordInfoImp = MutableLiveData<RecordInfo?>()
    val recordInfo : LiveData<RecordInfo?>
        get() = recordInfoImp

    private val firebaseCallback = object : FirebaseCallback(){
        override fun retrieveRecord(recordInfo: RecordInfo?) {
            super.retrieveRecord(recordInfo)
            recordInfoImp.value = recordInfo
        }
    }


    init{
        calendarImp.value = Calendar.getInstance(Locale.TAIWAN)
        recordInfoImp.value = RecordInfo(0f,0)
    }

    fun incrementDay(){
        calendarImp.value?.add(Calendar.DATE,1)
        calendarImp.notifyObserver()
    }

    fun decrementDay(){
        calendarImp.value?.add(Calendar.DATE,-1)
        calendarImp.notifyObserver()
    }

    fun setDate(year: Int,month: Int,day: Int){
        calendarImp.value?.set(year,month,day)
        calendarImp.notifyObserver()
    }

    fun searchRecord(date: String){
        FirebaseUtil.retrieveRecord(date,firebaseCallback)
    }

}