package com.leantv.launcher.presentation.home

import com.leantv.launcher.data.model.AppModel

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val favorites: List<AppModel>,
        val installedApps: List<AppModel>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

