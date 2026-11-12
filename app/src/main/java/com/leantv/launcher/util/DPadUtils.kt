package com.leantv.launcher.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.leantv.launcher.R
import com.leantv.launcher.data.model.AppModel

object DPadUtils {

    /**
     * Launch an application safely using its exact component intent.
     */
    fun launchApp(context: Context, app: AppModel) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(
                    if (app.isLeanback) Intent.CATEGORY_LEANBACK_LAUNCHER else Intent.CATEGORY_LAUNCHER
                )
                setClassName(app.packageName, app.activityName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: standard launch intent
            try {
                val fallbackIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (fallbackIntent != null) {
                    fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(fallbackIntent)
                } else {
                    Toast.makeText(context, "Cannot launch ${app.label}", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(context, "Error launching app: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Open system App Info screen for an installed package.
     */
    fun openAppInfo(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open App Info", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Request system uninstallation of an application.
     */
    fun requestUninstall(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot request uninstall", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Open system Default Home App settings screen on Android 12+.
     */
    fun openDefaultHomeSettings(activity: Activity) {
        try {
            // Android 12+ Home settings intent
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallback = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                activity.startActivity(fallback)
            } catch (ex: Exception) {
                try {
                    val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    activity.startActivity(settingsIntent)
                } catch (e2: Exception) {
                    Toast.makeText(activity, "Cannot open Settings", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

