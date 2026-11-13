package com.auratv.launcher

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.auratv.launcher.data.AppRepository
import com.auratv.launcher.data.IconDiskCache
import com.auratv.launcher.data.LauncherDataStoreRepository
import com.auratv.launcher.data.LauncherDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuraTVApplication : Application(), ImageLoaderFactory {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: LauncherDatabase
        private set
    lateinit var dataStoreRepository: LauncherDataStoreRepository
        private set
    lateinit var iconDiskCache: IconDiskCache
        private set
    lateinit var repository: AppRepository
        private set

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            .crossfade(100)
            .allowRgb565(true) // Saves 50% memory on TV GPUs
            .build()
    }

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
