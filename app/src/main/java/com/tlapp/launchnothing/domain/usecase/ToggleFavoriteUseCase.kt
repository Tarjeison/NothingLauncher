package com.tlapp.launchnothing.domain.usecase

import com.tlapp.launchnothing.data.repository.AppRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val appRepository: AppRepository,
) {
    suspend operator fun invoke(
        packageName: String,
        isFavorite: Boolean,
    ) {
        appRepository.setFavorite(packageName, isFavorite)
    }
}
