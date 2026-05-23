package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(navController: NavController, viewModel: InventoryViewModel) {
    val mostInefficient by viewModel.mostInefficientProduct.collectAsStateWithLifecycle()
    val highestDemand by viewModel.highestDemandProduct.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Predicciones y Aprendizaje") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Predicción de Pérdidas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val predictionText = if (mostInefficient != null) {
                        "Se espera pérdida de mercancía o bajos ingresos en productos como '${mostInefficient!!.name}' durante los próximos meses si la tendencia se mantiene, debido a su bajo margen o lenta rotación."
                    } else {
                        "Aún no hay suficientes datos para generar predicciones."
                    }
                    Text(text = predictionText)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val demandText = if (highestDemand != null) {
                        "Prepara un mayor abastecimiento de '${highestDemand!!.name}' antes del próximo trimestre, muestra un bajo stock con alto potencial de rotación."
                    } else {
                        ""
                    }
                    if (demandText.isNotBlank()) {
                        Text(text = demandText)
                    }
                }
            }

            Button(
                onClick = { viewModel.runPredictionAnalysis() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Generar Alerta de Predicción")
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text("Entrenamiento y Contexto", style = MaterialTheme.typography.titleMedium)
            Text("Carga datos adicionales para mejorar las predicciones (Fechas especiales, cambios de estación, etc).", style = MaterialTheme.typography.bodyMedium)
            
            OutlinedTextField(
                value = "", 
                onValueChange = {},
                label = { Text("Agrega variables del mercado") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )
            
            Button(
                onClick = { /* TODO: Save training context */ },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Cargar contexto de análisis")
            }
        }
    }
}
