package com.leantv.launcher.domain.usecase

import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.data.repository.AppRepository

class GetInstalledAppsUseCase(private val repository: AppRepository) {
    suspend operator fun invoke(includeHidden: Boolean = false): List<AppModel> {
        return repository.getInstalledApps(includeHidden = includeHidden)
    }
}

