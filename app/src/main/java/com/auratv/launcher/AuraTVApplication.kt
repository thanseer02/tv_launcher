package com.auratv.launcher

import android.app.Application
import com.auratv.launcher.data.AppRepository
import com.auratv.launcher.data.IconDiskCache
import com.auratv.launcher.data.LauncherDataStoreRepository
import com.auratv.launcher.data.LauncherDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuraTVApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: LauncherDatabase
        private set
    lateinit var dataStoreRepository: LauncherDataStoreRepository
        private set
    lateinit var iconDiskCache: IconDiskCache
        private set
    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = LauncherDatabase.getInstance(this)
        dataStoreRepository = LauncherDataStoreRepository(this)
        iconDiskCache = IconDiskCache(this)
        repository = AppRepository(this, database.appDao(), iconDiskCache)

        // Perform fast asynchronous initial sync without blocking main thread cold start
        applicationScope.launch {
            val profile = dataStoreRepository.profileFlow.first()
            repository.refreshApps(iconPackPackage = profile.customIconPackPackage, forceReloadIcons = false)
        }
    }
}
