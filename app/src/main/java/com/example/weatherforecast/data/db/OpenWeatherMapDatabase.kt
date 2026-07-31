package com.example.weatherforecast.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [DailyWeatherEntity::class, HourlyWeatherEntity::class], version = 5)
abstract class OpenWeatherMapDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: OpenWeatherMapDatabase? = null

        operator fun invoke(context: Context): OpenWeatherMapDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): OpenWeatherMapDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                OpenWeatherMapDatabase::class.java,
                "open_weather_map_database.db"
            ).addMigrations(MIGRATION_4_5).build()
        }

        /**
         * Explicit 4 -> 5 migration: v4 had `tzOffset` (dropped in v5) and lacked
         * the NOT NULL `latitude`/`longitude` columns (added in v5).
         * Table-recreate pattern; `PRAGMA defer_foreign_keys=ON` keeps the
         * hourly_weather -> daily_weather FK valid while the table is swapped.
         * No DEFAULT clauses so Room's TableInfo validation matches exactly.
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("PRAGMA defer_foreign_keys=ON")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `daily_weather_new` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`dew` REAL NOT NULL, " +
                        "`uvindex` INTEGER NOT NULL, " +
                        "`date` TEXT NOT NULL, " +
                        "`dt` INTEGER NOT NULL, " +
                        "`temp` REAL NOT NULL, " +
                        "`feelsLike` REAL NOT NULL, " +
                        "`tempMin` REAL NOT NULL, " +
                        "`tempMax` REAL NOT NULL, " +
                        "`pressure` REAL NOT NULL, " +
                        "`humidity` INTEGER NOT NULL, " +
                        "`windSpeed` REAL NOT NULL, " +
                        "`windDeg` INTEGER NOT NULL, " +
                        "`cloudiness` INTEGER NOT NULL, " +
                        "`description` TEXT NOT NULL, " +
                        "`icon` TEXT NOT NULL, " +
                        "`sunrise` INTEGER NOT NULL, " +
                        "`sunset` INTEGER NOT NULL, " +
                        "`moonPhase` REAL NOT NULL, " +
                        "`visibility` REAL NOT NULL, " +
                        "`cityName` TEXT, " +
                        "`timezone` TEXT NOT NULL, " +
                        "`latitude` REAL NOT NULL, " +
                        "`longitude` REAL NOT NULL)"
                )
                db.execSQL(
                    "INSERT INTO `daily_weather_new` (" +
                        "`id`,`dew`,`uvindex`,`date`,`dt`,`temp`,`feelsLike`,`tempMin`,`tempMax`," +
                        "`pressure`,`humidity`,`windSpeed`,`windDeg`,`cloudiness`,`description`,`icon`," +
                        "`sunrise`,`sunset`,`moonPhase`,`visibility`,`cityName`,`timezone`," +
                        "`latitude`,`longitude`) " +
                        "SELECT `id`,`dew`,`uvindex`,`date`,`dt`,`temp`,`feelsLike`,`tempMin`,`tempMax`," +
                        "`pressure`,`humidity`,`windSpeed`,`windDeg`,`cloudiness`,`description`,`icon`," +
                        "`sunrise`,`sunset`,`moonPhase`,`visibility`,`cityName`,`timezone`,0,0 " +
                        "FROM `daily_weather`"
                )
                db.execSQL("DROP TABLE `daily_weather`")
                db.execSQL("ALTER TABLE `daily_weather_new` RENAME TO `daily_weather`")
            }
        }
    }
}

