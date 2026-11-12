package com.leantv.launcher.data.model

import android.graphics.drawable.Drawable

/**
 * Lightweight in-memory representation of an installed Android TV or standard application.
 *
 * To minimize heap footprint on 2 GB RAM TV devices:
 * - Icons/banners are NOT stored eagerly in this data class.
 * - Icon resolution is deferred to memory-capped LruCache.
 */
data class AppModel(
    val label: String,
    val packageName: String,
    val activityName: String,
    val isLeanback: Boolean,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val firstInstallTime: Long = 0L,
    val lastUpdateTime: Long = 0L,
    val launchCount: Int = 0
) {
    /** Unique component key to distinguish multi-activity applications */
    val componentKey: String
        get() = "$packageName/$activityName"
}

