package com.auratv.launcher.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IconDiskCache(private val context: Context) {
    private val iconDir: File by lazy {
        File(context.filesDir, "app_icons").apply { if (!exists()) mkdirs() }
    }
    private val bannerDir: File by lazy {
        File(context.filesDir, "app_banners").apply { if (!exists()) mkdirs() }
    }

    suspend fun saveDrawable(drawable: Drawable, packageName: String, isBanner: Boolean): String = withContext(Dispatchers.IO) {
        val targetDir = if (isBanner) bannerDir else iconDir
        val file = File(targetDir, "${packageName.replace('.', '_')}.png")
        if (file.exists() && file.length() > 0) {
            return@withContext file.absolutePath
        }

        val bitmap = drawableToBitmap(drawable, isBanner)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        }
        file.absolutePath
    }

    suspend fun extractDominantColor(drawable: Drawable): Int = withContext(Dispatchers.Default) {
        val bitmap = drawableToBitmap(drawable, isBanner = false)
        val palette = Palette.from(bitmap).maximumColorCount(16).generate()
        palette.getDarkMutedColor(
            palette.getDarkVibrantColor(
                palette.getDominantColor(0xFF1E293B.toInt())
            )
        )
    }

    fun removeCachedFiles(packageName: String) {
        val iconFile = File(iconDir, "${packageName.replace('.', '_')}.png")
        if (iconFile.exists()) iconFile.delete()
        val bannerFile = File(bannerDir, "${packageName.replace('.', '_')}.png")
        if (bannerFile.exists()) bannerFile.delete()
    }

    private fun drawableToBitmap(drawable: Drawable, isBanner: Boolean): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else (if (isBanner) 320 else 128)
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else (if (isBanner) 180 else 128)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

