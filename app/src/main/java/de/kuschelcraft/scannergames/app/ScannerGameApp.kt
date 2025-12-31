package de.kuschelcraft.scannergames.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.kuschelcraft.scannergames.navigation.BottomNavigationBar
import de.kuschelcraft.scannergames.navigation.BottomTab
import de.kuschelcraft.scannergames.screen.LeaderboardScreen
import de.kuschelcraft.scannergames.screen.PlayScreen
import de.kuschelcraft.scannergames.viewmodel.LeaderboardViewModel

@Composable
fun ScannerGameApp(viewModel: LeaderboardViewModel) {
    var selectedTab by remember() { mutableStateOf(BottomTab.Play) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                BottomTab.Play -> PlayScreen(viewModel)
                BottomTab.Leaderboard -> LeaderboardScreen(viewModel)
            }
        }
    }
}