package com.leantv.launcher.domain.usecase

import com.leantv.launcher.data.repository.AppRepository

class ToggleFavoriteUseCase(private val repository: AppRepository) {
    suspend operator fun invoke(componentKey: String): Boolean {
        return repository.toggleFavorite(componentKey)
    }
}

