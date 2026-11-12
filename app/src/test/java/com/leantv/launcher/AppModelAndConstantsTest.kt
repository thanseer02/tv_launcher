package com.leantv.launcher

import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.util.AppConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppModelAndConstantsTest {

    @Test
    fun testAppModelComponentKey() {
        val app = AppModel(
            label = "YouTube",
            packageName = "com.google.android.youtube.tv",
            activityName = "com.google.android.apps.youtube.tv.activity.ShellActivity",
            isLeanback = true,
            isFavorite = true
        )

        assertEquals("com.google.android.youtube.tv/com.google.android.apps.youtube.tv.activity.ShellActivity", app.componentKey)
        assertTrue(app.isFavorite)
        assertFalse(app.isHidden)
    }

    @Test
    fun testConstantsBounds() {
        assertEquals(5, AppConstants.DEFAULT_GRID_COLUMNS)
        assertTrue(AppConstants.MIN_GRID_COLUMNS <= AppConstants.DEFAULT_GRID_COLUMNS)
        assertTrue(AppConstants.MAX_GRID_COLUMNS >= AppConstants.DEFAULT_GRID_COLUMNS)
        assertEquals("OLED", AppConstants.THEME_OLED)
    }

    @Test
    fun testAppListSorting() {
        val apps = listOf(
            AppModel("Netflix", "com.netflix.ninja", "MainActivity", isLeanback = true),
            AppModel("Amazon Prime", "com.amazon.amazonvideo.livingroom", "MainActivity", isLeanback = true),
            AppModel("YouTube", "com.google.android.youtube.tv", "MainActivity", isLeanback = true)
        )

        val sortedAz = apps.sortedBy { it.label.lowercase() }
        assertEquals("Amazon Prime", sortedAz[0].label)
        assertEquals("Netflix", sortedAz[1].label)
        assertEquals("YouTube", sortedAz[2].label)

        val sortedZa = apps.sortedByDescending { it.label.lowercase() }
        assertEquals("YouTube", sortedZa[0].label)
        assertEquals("Netflix", sortedZa[1].label)
        assertEquals("Amazon Prime", sortedZa[2].label)
    }
}

