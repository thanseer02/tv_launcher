package com.leantv.launcher.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.collection.LruCache

/**
 * Low-RAM bounded LRU Cache for TV App Drawables / Banners.
 * Ensures the TV launcher never consumes more than 8MB of native bitmap memory,
 * leaving maximum RAM for streaming apps on 2GB Mi TVs.
 */
object MemoryCache {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt() // in KB
    // Use at most 8MB or 1/16th of heap, whichever is smaller
    private val cacheSize = minOf(8 * 1024, maxMemory / 16)

    private val drawableCache = object : LruCache<String, Drawable>(cacheSize) {
        override fun sizeOf(key: String, value: Drawable): Int {
            return when (value) {
                is BitmapDrawable -> {
                    val bitmap = value.bitmap
                    if (bitmap != null && !bitmap.isRecycled) {
                        bitmap.byteCount / 1024
                    } else 64
                }
                else -> 64 // standard fallback estimate in KB
            }
        }
    }

    @Synchronized
    fun get(key: String): Drawable? {
        return drawableCache.get(key)
    }

    @Synchronized
    fun put(key: String, drawable: Drawable) {
        if (drawableCache.get(key) == null) {
            drawableCache.put(key, drawable)
        }
    }

    @Synchronized
    fun remove(key: String) {
        drawableCache.remove(key)
    }

    @Synchronized
    fun clear() {
        drawableCache.evictAll()
    }
}

