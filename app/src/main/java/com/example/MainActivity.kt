package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.InventoryRepository
import com.example.data.SettingsManager
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.NotificationHelper
import com.example.viewmodel.InventoryViewModel
import com.example.viewmodel.InventoryViewModelFactory
import com.example.ui.LoginScreen
import com.example.ui.HomeScreen
import com.example.ui.AddDataScreen
import com.example.ui.AnalysisScreen
import com.example.ui.PredictionScreen
import com.example.ui.SettingsScreen
import com.example.ui.HistoryScreen
import com.example.ui.SummaryScreen

import com.example.ui.InvoiceScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = InventoryRepository(database.productDao(), database.inventoryLogDao())
        val settingsManager = SettingsManager(this)
        val notificationHelper = NotificationHelper(this)
        val factory = InventoryViewModelFactory(repository, settingsManager, notificationHelper)
        
        // Request post notification permission on Tiramisu+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
        
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: InventoryViewModel = viewModel(factory = factory)
                    
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { LoginScreen(navController) }
                        composable("home") { HomeScreen(navController, viewModel) }
                        composable("add") { AddDataScreen(navController, viewModel) }
                        composable("analysis") { AnalysisScreen(navController, viewModel) }
                        composable("prediction") { PredictionScreen(navController, viewModel) }
                        composable("settings") { SettingsScreen(navController, viewModel) }
                        composable("history") { HistoryScreen(navController, viewModel) }
                        composable("summary") { SummaryScreen(navController, viewModel) }
                        composable("invoices") { InvoiceScreen(navController, viewModel) }
                    }
                }
            }
        }
    }
}