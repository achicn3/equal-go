package com.local.local.screen.fragment.ui.firends

import androidx.lifecycle.ViewModel
import com.kdanmobile.cloud.event.EventBroadcaster
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.util.FirebaseUtil

class AddFriendViewModel(
    private val eventManager: EventManager<Event> = EventManager()
) : ViewModel(), EventBroadcaster<AddFriendViewModel.Event> by eventManager {

    sealed class Event {
        class OnSearchStart() : Event()
        class OnSearchFinish() : Event()
        class OnSearchSuc() : Event()
        class OnSearchFail() : Event()
        class OnSearchError() : Event()
        class OnAddStart() : Event()
        class OnAddFinish() : Event()
        class OnFriendsAlreadyAdded : Event()
        class OnAddSuc() : Event()
        class OnAddFail() : Event()
        class OnAddError() : Event()
    }

    var searchedUserInfo: UserInfo? = null
    private val firebaseCallback: FirebaseCallback

    init {
        firebaseCallback = object : FirebaseCallback() {
            override fun isPhoneExisted(phoneNumber: String?, response: Boolean?) {
                super.isPhoneExisted(phoneNumber, response)
                Event.OnSearchFinish().send()
                when (response) {
                    true -> {
                        getUserInfoByPhone(phoneNumber)
                    }
                    false -> {
                        Event.OnSearchFail().send()
                    }
                    else -> {
                        Event.OnSearchError().send()
                    }
                }
            }

            override fun getUserInfoByPhone(userInfo: UserInfo?) {
                super.getUserInfoByPhone(userInfo)
                if (userInfo == null) {
                    Event.OnSearchError().send()
                } else {
                    Event.OnSearchSuc().send()
                    searchedUserInfo = userInfo
                }

            }

            override fun isKeyExisted(userKey: String?, response: Boolean?) {
                super.isKeyExisted(userKey, response)
                Event.OnSearchFinish().send()
                when (response) {
                    true -> {
                        getUserInfoByKey(userKey)
                    }
                    false -> {
                        Event.OnSearchFail().send()
                    }
                    else -> {
                        Event.OnSearchError().send()
                    }
                }
            }

            override fun getUserInfoByKey(userInfo: UserInfo?) {
                super.getUserInfoByKey(userInfo)
                if (userInfo == null) {
                    Event.OnSearchError().send()
                } else {
                    Event.OnSearchSuc().send()
                    searchedUserInfo = userInfo
                }
            }

            override fun addFriendResponse(response: Boolean?) {
                super.addFriendResponse(response)
                Event.OnAddFinish().send()
                response ?: Event.OnAddError().send()
                when (response) {
                    true -> {
                        Event.OnAddSuc().send()
                    }
                    false -> {
                        Event.OnAddFail().send()
                    }
                }
            }

            override fun isFriendsAdded(response: Boolean?) {
                super.isFriendsAdded(response)
                response ?: Event.OnAddError().send()
                when (response) {
                    true -> {
                        Event.OnFriendsAlreadyAdded().send()
                    }
                    false -> {
                        FirebaseUtil.addFriends(searchedUserInfo, this)
                    }
                }
            }
        }
    }


    private fun getUserInfoByPhone(phoneNumber: String?) {
        FirebaseUtil.getUserInfoByPhone(phoneNumber, firebaseCallback)
    }

    private fun getUserInfoByKey(userKey: String?) {
        FirebaseUtil.getUserInfoByKey(userKey, firebaseCallback)
    }


    private fun Event.send() {
        eventManager.send(this)
    }

    fun searchByPhoneNumber(phoneNumber: String) {
        searchedUserInfo = null
        Event.OnSearchStart().send()
        FirebaseUtil.isPhoneExisted(phoneNumber, firebaseCallback)
    }

    fun searchByQRCode(userKey: String?) {
        searchedUserInfo = null
        Event.OnSearchStart().send()
        FirebaseUtil.isKeyExisted(userKey, firebaseCallback)
    }

    fun onClickAdd() {
        Event.OnAddStart().send()
        if (searchedUserInfo != null) {
            FirebaseUtil.checkFriendsAlreadyAdd(searchedUserInfo,firebaseCallback)
        } else {
            Event.OnAddFinish().send()
            Event.OnAddError().send()
        }
    }

}