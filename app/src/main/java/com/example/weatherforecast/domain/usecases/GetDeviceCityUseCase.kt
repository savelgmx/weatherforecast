package com.example.weatherforecast.domain.usecases


import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import javax.inject.Inject

/**
 * Юзкейс для получения названия города устройства.
 */
class GetDeviceCityUseCase @Inject constructor(
    private val repository: VisualCrossingRepository
) {
    /**
     * Выполняет запрос для получения названия города устройства.
     * @return Название города или дефолтное значение.
     */
    suspend fun execute(): String {
        return repository.getDeviceCity()
    }
}