package de.kuschelcraft.scannergames.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow

enum class BottomTab(val label: String, val icon: ImageVector) {
    Play("Play", Icons.Default.PlayArrow),

    Leaderboard("Leaderboard", Icons.Default.Leaderboard)
}