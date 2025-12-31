package de.kuschelcraft.scannergames.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY points DESC")
    fun getLeaderboard(): Flow<List<LeaderboardItem>>

    @Insert
    suspend fun insertItem(item: LeaderboardItem)

    @Delete
    suspend fun deleteItem(item: LeaderboardItem)

    @Query("DELETE FROM leaderboard") // Passe den Tabellennamen an
    suspend fun deleteAll()
}