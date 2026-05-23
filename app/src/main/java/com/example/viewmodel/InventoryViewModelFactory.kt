package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.InventoryRepository
import com.example.data.SettingsManager
import com.example.utils.NotificationHelper

class InventoryViewModelFactory(
    private val repository: InventoryRepository,
    private val settingsManager: SettingsManager,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(repository, settingsManager, notificationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
