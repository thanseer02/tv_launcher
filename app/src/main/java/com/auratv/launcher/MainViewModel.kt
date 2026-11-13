package com.auratv.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.auratv.launcher.data.AppRepository
import com.auratv.launcher.data.LauncherDataStoreRepository
import com.auratv.launcher.model.AppItem
import com.auratv.launcher.model.AutoSortOption
import com.auratv.launcher.model.LauncherProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = getApplication<AuraTVApplication>()
    private val repository: AppRepository = app.repository
    private val dataStoreRepository: LauncherDataStoreRepository = app.dataStoreRepository

    val profile: StateFlow<LauncherProfile> = dataStoreRepository.profileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LauncherProfile(name = "Active")
    )

    // Sorted and filtered visible apps based on AutoSortOption in profile
    val visibleApps: StateFlow<List<AppItem>> = combine(
        repository.visibleApps,
        profile
    ) { apps, prof ->
        when (prof.autoSort) {
            AutoSortOption.ALPHABETICAL -> apps.sortedBy { it.displayLabel.lowercase() }
            AutoSortOption.MOST_USED -> apps.sortedByDescending { it.launchCount }
            AutoSortOption.RECENTLY_INSTALLED -> apps.sortedByDescending { it.installTime }
            AutoSortOption.MANUAL -> apps
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allApps: StateFlow<List<AppItem>> = repository.allApps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteApps: StateFlow<List<AppItem>> = repository.favoriteApps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentApps: StateFlow<List<AppItem>> = repository.recentApps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedAppForMenu = MutableStateFlow<AppItem?>(null)
    val selectedAppForMenu: StateFlow<AppItem?> = _selectedAppForMenu.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isReorderingFavorites = MutableStateFlow(false)
    val isReorderingFavorites: StateFlow<Boolean> = _isReorderingFavorites.asStateFlow()

    fun openAppMenu(app: AppItem) {
        _selectedAppForMenu.value = app
    }

    fun closeAppMenu() {
        _selectedAppForMenu.value = null
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun updateProfile(newProfile: LauncherProfile) {
        viewModelScope.launch {
            dataStoreRepository.saveProfile(newProfile)
        }
    }

    fun toggleFavorite(app: AppItem) {
        viewModelScope.launch {
            repository.toggleFavorite(app.packageName)
        }
    }

    fun toggleHideApp(app: AppItem) {
        viewModelScope.launch {
            repository.toggleHidden(app.packageName)
        }
    }

    fun renameApp(app: AppItem, newName: String) {
        viewModelScope.launch {
            repository.updateCustomLabel(app.packageName, newName)
        }
    }

    fun setAppCustomIcon(app: AppItem, iconPath: String?) {
        viewModelScope.launch {
            repository.updateCustomIcon(app.packageName, iconPath)
        }
    }

    fun recordAppLaunch(app: AppItem) {
        viewModelScope.launch {
            repository.recordAppLaunch(app.packageName)
        }
    }

    fun resetDefaults() {
        viewModelScope.launch {
            dataStoreRepository.resetToDefault()
            repository.resetCustomizations()
        }
    }

    fun startReorderingFavorites() {
        _isReorderingFavorites.value = true
    }

    fun finishReorderingFavorites() {
        _isReorderingFavorites.value = false
    }

    fun moveFavorite(app: AppItem, direction: Int) {
        val currentFavorites = favoriteApps.value.toMutableList()
        val index = currentFavorites.indexOfFirst { it.packageName == app.packageName }
        if (index == -1) return

        val newIndex = index + direction
        if (newIndex in currentFavorites.indices) {
            currentFavorites.removeAt(index)
            currentFavorites.add(newIndex, app)
            viewModelScope.launch {
                repository.reorderFavorites(currentFavorites)
            }
        }
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = dataStoreRepository.exportJson(profile.value)
            onResult(json)
        }
    }

    fun importBackup(json: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                dataStoreRepository.importJson(json)
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
