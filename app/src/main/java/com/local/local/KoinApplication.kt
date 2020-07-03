package com.local.local

import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.local.local.retrofit.ImageUploadServiceHolder
import com.local.local.retrofit.services.ServiceBuilder
import com.local.local.screen.admin.userlist.UserListViewModel
import com.local.local.screen.admin.verification.VerificationViewModel
import com.local.local.screen.fragment.ui.firends.addfriend.AddFriendViewModel
import com.local.local.screen.fragment.ui.firends.friendlsit.FriendListViewModel
import com.local.local.screen.fragment.ui.points.daystatics.HomeViewModel
import com.local.local.screen.fragment.ui.points.detailstatics.StaticsViewModel
import com.local.local.screen.fragment.ui.points.transaction.exchange.TransactionViewModel
import com.local.local.screen.fragment.ui.points.transaction.record.TransactionRecordViewModel
import com.local.local.screen.fragment.ui.profile.ProfileInfoViewModel
import com.local.local.screen.login.LoginViewModel
import com.local.local.screen.store.login.StoreLoginViewModel
import com.local.local.screen.register.RegisterViewModel
import com.local.local.screen.store.items.AddItemViewModel
import com.local.local.screen.store.items.StoreAddItemViewModel
import com.local.local.screen.store.record.StoreRecordViewModel
import com.local.local.screen.store.register.StoreRegisterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class KoinApplication : MultiDexApplication() {
    val m: Module = module {
        viewModel { (activity: Activity) -> LoginViewModel(get(),activity) }
        viewModel { (activity: Activity) -> RegisterViewModel(get(), activity) }
        viewModel { (activity: Activity) ->
            StoreLoginViewModel(
                activity
            )
        }
        viewModel { AddItemViewModel() }
        viewModel { StoreRegisterViewModel() }
        viewModel { StoreRecordViewModel() }
        viewModel { AddFriendViewModel() }
        viewModel { FriendListViewModel(get()) }
        viewModel { ProfileInfoViewModel() }
        viewModel { HomeViewModel() }
        viewModel { StaticsViewModel() }
        viewModel { TransactionViewModel() }
        viewModel { StoreAddItemViewModel() }
        viewModel { TransactionRecordViewModel() }
        viewModel { VerificationViewModel() }
        viewModel { UserListViewModel() }
        single { ServiceBuilder.buildOkHttpClient() }
        single { ImageUploadServiceHolder(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApplication)
            modules(m)
        }
    }
}