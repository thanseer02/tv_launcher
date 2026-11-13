package com.auratv.launcher.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.auratv.launcher.model.AppItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM app_preferences WHERE isHidden = 0 ORDER BY label COLLATE NOCASE ASC")
    fun getAllVisibleApps(): Flow<List<AppItem>>

    @Query("SELECT * FROM app_preferences ORDER BY label COLLATE NOCASE ASC")
    fun getAllApps(): Flow<List<AppItem>>

    @Query("SELECT * FROM app_preferences WHERE isFavorite = 1 AND isHidden = 0 ORDER BY favoriteOrder ASC")
    fun getFavoriteApps(): Flow<List<AppItem>>

    @Query("SELECT * FROM app_preferences WHERE lastLaunchTime > 0 AND isHidden = 0 ORDER BY lastLaunchTime DESC LIMIT 15")
    fun getRecentApps(): Flow<List<AppItem>>

    @Query("SELECT * FROM app_preferences WHERE packageName = :packageName LIMIT 1")
    suspend fun getApp(packageName: String): AppItem?

    @Query("SELECT * FROM app_preferences")
    suspend fun getAllAppsSnapshot(): List<AppItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(apps: List<AppItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: AppItem)

    @Update
    suspend fun update(app: AppItem)

    @Query("DELETE FROM app_preferences WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("UPDATE app_preferences SET isFavorite = :isFavorite, favoriteOrder = :order WHERE packageName = :packageName")
    suspend fun setFavorite(packageName: String, isFavorite: Boolean, order: Int = Int.MAX_VALUE)

    @Query("UPDATE app_preferences SET isHidden = :isHidden WHERE packageName = :packageName")
    suspend fun setHidden(packageName: String, isHidden: Boolean)

    @Query("UPDATE app_preferences SET customLabel = :label WHERE packageName = :packageName")
    suspend fun setCustomLabel(packageName: String, label: String?)

    @Query("UPDATE app_preferences SET customIconPath = :iconPath WHERE packageName = :packageName")
    suspend fun setCustomIcon(packageName: String, iconPath: String?)

    @Query("UPDATE app_preferences SET lastLaunchTime = :launchTime, launchCount = launchCount + 1 WHERE packageName = :packageName")
    suspend fun recordLaunch(packageName: String, launchTime: Long = System.currentTimeMillis())

    @Query("UPDATE app_preferences SET isFavorite = 0, isHidden = 0, favoriteOrder = 2147483647, customLabel = NULL, customIconPath = NULL")
    suspend fun resetCustomizations()
}
