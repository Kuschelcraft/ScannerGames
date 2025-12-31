package de.kuschelcraft.scannergames.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LeaderboardItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LeaderboardDatabase : RoomDatabase() {
    abstract fun leaderboardDao(): LeaderboardDao

    companion object {
        @Volatile
        private var INSTANCE: LeaderboardDatabase? = null

        fun getDatabase(context: Context): LeaderboardDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    LeaderboardDatabase::class.java,
                    "leaderboard_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}