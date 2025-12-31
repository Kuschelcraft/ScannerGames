package de.kuschelcraft.scannergames.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.kuschelcraft.scannergames.data.LeaderboardDao
import de.kuschelcraft.scannergames.data.LeaderboardItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LeaderboardViewModel(private val dao: LeaderboardDao) : ViewModel() {

    val leaderboard: StateFlow<List<LeaderboardItem>> = dao.getLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(name: String, points: Int) {
        viewModelScope.launch {
            val newItem = LeaderboardItem(
                name = name,
                points = points
            )
            dao.insertItem(newItem)
        }
    }

    fun clearLeaderboard() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }

}