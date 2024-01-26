

import com.example.weatherforecast.api.OpenWeatherMapAPI
import com.example.weatherforecast.api.OpenWeatherMapRepository
import com.example.weatherforecast.db.OpenWeatherMapDatabase
import com.example.weatherforecast.api.OpenWeatherMapRepositoryImpl
import com.example.weatherforecast.di.NetworkObject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAPI() : OpenWeatherMapAPI {
        return NetworkObject.getAPIInstance()
    }

    @Provides
    @Singleton
    fun provideOpenWeatherMapRepository(api: OpenWeatherMapAPI, database: OpenWeatherMapDatabase): OpenWeatherMapRepository {
        return OpenWeatherMapRepositoryImpl(api, database.openWeatherMapDao())
    }
}
