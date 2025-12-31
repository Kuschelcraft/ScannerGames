package de.kuschelcraft.scannergames

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kuschelcraft.scannergames.app.ScannerGameApp
import de.kuschelcraft.scannergames.data.LeaderboardDatabase
import de.kuschelcraft.scannergames.viewmodel.LeaderboardViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val context = LocalContext.current

                val db = LeaderboardDatabase.getDatabase(context)
                val dao = db.leaderboardDao()

                val viewModel: LeaderboardViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
                                return LeaderboardViewModel(dao) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                    }
                )

                ScannerGameApp(viewModel)
            }
        }
    }
}