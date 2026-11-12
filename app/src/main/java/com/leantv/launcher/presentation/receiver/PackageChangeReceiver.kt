package com.leantv.launcher.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.leantv.launcher.LeanTVApplication

/**
 * BroadcastReceiver triggered when applications are installed, updated, or removed on the TV.
 * Evicts invalidated icon cache entries to keep memory usage minimal.
 */
class PackageChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val packageName = intent.data?.schemeSpecificPart

        when (action) {
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_ADDED -> {
                try {
                    val app = LeanTVApplication.instance
                    if (!packageName.isNullOrEmpty()) {
                        app.appRepository.evictIcon(packageName)
                    }
                    app.appRepository.clearIconCache()
                } catch (ignored: Exception) {}
            }
        }
    }
}

