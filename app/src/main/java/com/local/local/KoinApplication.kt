package com.local.local

import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.local.local.screen.login.LoginViewModel
import com.local.local.screen.register.RegisterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class KoinApplication : MultiDexApplication() {
    val m: Module = module {
        viewModel { (activity: Activity) -> LoginViewModel(this@KoinApplication,activity) }
        viewModel { (activity: Activity) -> RegisterViewModel(get(), activity) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApplication)
            modules(m)
        }
    }
}