package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: InventoryViewModel) {
    val settingsManager = viewModel.settingsManager

    var notifyLowStock by remember { mutableStateOf(settingsManager.notifyLowStock) }
    var notifyPredictions by remember { mutableStateOf(settingsManager.notifyPredictions) }
    var notifyInventoryChanges by remember { mutableStateOf(settingsManager.notifyInventoryChanges) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración de Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingSwitchRow(
                title = "Alerta de Stock Bajo",
                description = "Notificar cuando el stock de un producto es menor a 10 unidades.",
                checked = notifyLowStock,
                onCheckedChange = { 
                    notifyLowStock = it 
                    settingsManager.notifyLowStock = it 
                }
            )

            SettingSwitchRow(
                title = "Predicción de Pérdidas",
                description = "Notificar sobre productos ineficientes o posibles pérdidas.",
                checked = notifyPredictions,
                onCheckedChange = { 
                    notifyPredictions = it 
                    settingsManager.notifyPredictions = it 
                }
            )

            SettingSwitchRow(
                title = "Cambios Importantes",
                description = "Notificar cuando se agreguen o actualicen productos.",
                checked = notifyInventoryChanges,
                onCheckedChange = { 
                    notifyInventoryChanges = it 
                    settingsManager.notifyInventoryChanges = it 
                }
            )
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
