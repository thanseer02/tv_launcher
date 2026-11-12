package com.leantv.launcher.presentation.home

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.data.repository.AppRepository
import com.leantv.launcher.domain.usecase.GetInstalledAppsUseCase
import com.leantv.launcher.domain.usecase.ToggleFavoriteUseCase
import com.leantv.launcher.domain.usecase.ToggleHideAppUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val appRepository: AppRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val toggleHideAppUseCase: ToggleHideAppUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadApps() {
        viewModelScope.launch {
            try {
                val allApps = getInstalledAppsUseCase(includeHidden = false)
                val favorites = allApps.filter { it.isFavorite }
                val installed = allApps.filter { !it.isFavorite }
                _uiState.value = HomeUiState.Success(favorites = favorites, installedApps = installed)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load TV applications")
            }
        }
    }

    fun toggleFavorite(app: AppModel) {
        viewModelScope.launch {
            toggleFavoriteUseCase(app.componentKey)
            loadApps()
        }
    }

    fun hideApp(app: AppModel) {
        viewModelScope.launch {
            toggleHideAppUseCase(app.componentKey, isHidden = true)
            loadApps()
        }
    }

    suspend fun loadIcon(app: AppModel): Drawable? {
        return appRepository.loadAppIcon(app)
    }
}

