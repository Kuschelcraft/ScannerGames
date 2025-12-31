package de.kuschelcraft.scannergames.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "leaderboard"
)
data class LeaderboardItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val points: Int
)