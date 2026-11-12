package com.leantv.launcher.domain.usecase

import com.leantv.launcher.data.repository.AppRepository

class ToggleHideAppUseCase(private val repository: AppRepository) {
    suspend operator fun invoke(componentKey: String, isHidden: Boolean) {
        repository.setAppHidden(componentKey, isHidden)
    }
}

