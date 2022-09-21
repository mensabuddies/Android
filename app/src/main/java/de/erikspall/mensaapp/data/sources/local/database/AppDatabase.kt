package de.erikspall.mensaapp.data.sources.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.erikspall.mensaapp.data.sources.local.database.daos.*
import de.erikspall.mensaapp.data.sources.local.database.entities.*

@Database(
    entities = [
        FoodProvider::class,
        Location::class,
        OpeningHours::class,
        Weekday::class,
        FoodProviderType::class
    ],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodProviderDao(): FoodProviderDao
    abstract fun locationDao(): LocationDao
    abstract fun openingHoursDao(): OpeningHoursDao
    abstract fun weekdayDao(): WeekdayDao
    abstract fun foodProviderTypeDao(): FoodProviderTypeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // TODO: remove before release
                context.applicationContext.deleteDatabase("app_database")

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}