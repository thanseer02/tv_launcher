package com.auratv.launcher.data

import android.content.Context
import android.content.Intent
import com.auratv.launcher.MainActivity
import com.auratv.launcher.model.LauncherProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            val dataStoreRepo = LauncherDataStoreRepository(context.applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                val profile = dataStoreRepo.profileFlow.first()
                val autoLaunchPackage = profile.autoLaunchBootPackage
                if (!autoLaunchPackage.isNullOrEmpty()) {
                    val pm = context.packageManager
                    val launchIntent = pm.getLeanbackLaunchIntentForPackage(autoLaunchPackage)
                        ?: pm.getLaunchIntentForPackage(autoLaunchPackage)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        return@launch
                    }
                }

                // If no auto-launch target is set, start the launcher activity to keep it warm in memory
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(mainIntent)
            }
        }
    }
}

