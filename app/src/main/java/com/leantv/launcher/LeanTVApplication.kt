package com.leantv.launcher

import android.app.Application
import com.leantv.launcher.data.repository.AppRepository
import com.leantv.launcher.data.source.PreferencesDataSource

class LeanTVApplication : Application() {

    lateinit var preferencesDataSource: PreferencesDataSource
        private set

    lateinit var appRepository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesDataSource = PreferencesDataSource(this)
        appRepository = AppRepository(this, preferencesDataSource)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Aggressively free bitmap cache on low RAM triggers (essential on 2 GB Xiaomi TVs)
        if (level >= TRIM_MEMORY_MODERATE) {
            appRepository.clearIconCache()
        }
    }

    companion object {
        lateinit var instance: LeanTVApplication
            private set
    }
}

