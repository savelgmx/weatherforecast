package com.example.weatherforecast.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/**
 * Room DAO for the weather cache (daily_weather + hourly_weather tables).
 *
 * All cache reads/writes are scoped to a single [city] so that different cities
 * (main screen vs. forecast screen vs. manually selected cities) never clobber
 * each other's cached rows. This is the "cache per city" fix (review item 2).
 */
@Dao
interface WeatherDao {

    /** Inserts a single daily row and returns its auto-generated primary key. */
    @Insert
    suspend fun insertDailyWeather(daily: DailyWeatherEntity): Long

    /** Inserts a batch of hourly rows. Callers must assign `dailyId` before insertion. */
    @Insert
    suspend fun insertHourlyWeather(hourlies: List<HourlyWeatherEntity>)

    /** LiveData-backed observable over ALL daily rows, used by the UI layer. */
    @Query("SELECT * FROM daily_weather")
    fun getAllDailyWeather(): LiveData<List<DailyWeatherEntity>>

    /** Loads the hourly rows that belong to a single daily row (by its [dailyId]). */
    @Query("SELECT * FROM hourly_weather WHERE dailyId = :dailyId")
    suspend fun getHourlyWeatherForDay(dailyId: Int): List<HourlyWeatherEntity>

    // --- Per-city queries ---

    /**
     * Returns the most recent daily row for a city.
     * Used for the "current weather" cache read.
     */
    @Query("SELECT * FROM daily_weather WHERE cityName = :city ORDER BY date ASC LIMIT 1")
    suspend fun getDailyWeatherByCity(city: String): DailyWeatherEntity?

    /**
     * Whether this city has any cached data at all (used to decide
     * between a network fetch and a cache hit in the repository).
     */
    @Query("SELECT COUNT(*) FROM daily_weather WHERE cityName = :city")
    suspend fun getDailyWeatherCountByCity(city: String): Int

    /**
     * Most recent cache timestamp (`dt`) for a city, used for staleness checks.
     * Returns null when the city has no cached rows.
     */
    @Query("SELECT MAX(dt) FROM daily_weather WHERE cityName = :city")
    suspend fun getLastUpdateTimeByCity(city: String): Long?

    /** Synchronous read of every cached daily row for a city (forecast screen). */
    @Query("SELECT * FROM daily_weather WHERE cityName = :city")
    fun getAllDailyWeatherByCity(city: String): List<DailyWeatherEntity>

    /**
     * Deletes the hourly rows that belong to any daily row of [city].
     *
     * NOTE: this must run BEFORE [deleteDailyWeatherForCity]. `hourly_weather` declares a
     * foreign key on `daily_weather.id` (onDelete = CASCADE); deleting the parent rows first
     * would still cascade-remove the children, but keeping the ordering explicit makes the
     * intent clear and avoids any FK-RESTRICT surface if the schema ever changes.
     */
    @Query("DELETE FROM hourly_weather WHERE dailyId IN (SELECT id FROM daily_weather WHERE cityName = :city)")
    suspend fun deleteHourlyWeatherForCity(city: String)

    /** Deletes all daily rows for [city]. */
    @Query("DELETE FROM daily_weather WHERE cityName = :city")
    suspend fun deleteDailyWeatherForCity(city: String)

    /**
     * Atomically removes every cached row for [city] (hourly first, then daily).
     * Used by force-refresh, stale-refresh and sync paths so only the target city's
     * cache is replaced instead of the whole database.
     */
    @Transaction
    suspend fun deleteCityWeather(city: String) {
        deleteHourlyWeatherForCity(city)
        deleteDailyWeatherForCity(city)
    }

    /**
     * Atomically inserts a daily row plus all of its hourly rows.
     * The hourly rows get the auto-generated daily id backfilled inside the transaction.
     */
    @Transaction
    suspend fun insertWeatherTransaction(dailyWeather: DailyWeatherEntity, hourlyWeathers: List<HourlyWeatherEntity>) {
        val dailyId = insertDailyWeather(dailyWeather)
        val updatedHourly = hourlyWeathers.map { it.copy(dailyId = dailyId.toInt()) }
        insertHourlyWeather(updatedHourly)
    }
}