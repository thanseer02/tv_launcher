package com.auratv.launcher.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.auratv.launcher.AuraTVApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart ?: return
        val app = context.applicationContext as AuraTVApplication
        val repository = app.repository

        when (intent.action) {
            Intent.ACTION_PACKAGE_REMOVED -> {
                val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (!replacing) {
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.onPackageRemoved(packageName)
                    }
                }
            }
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.onPackageAddedOrUpdated(packageName)
                }
            }
        }
    }
}

