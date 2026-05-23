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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(navController: NavController, viewModel: InventoryViewModel) {
    val totalInvestment by viewModel.totalInvestment.collectAsStateWithLifecycle()
    val expectedRevenue by viewModel.expectedRevenue.collectAsStateWithLifecycle()
    val highestDemand by viewModel.highestDemandProduct.collectAsStateWithLifecycle()
    val mostInefficient by viewModel.mostInefficientProduct.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis Automático") },
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
            AnalysisCard(
                title = "Total Inversión (Compras)",
                content = "$${"%.2f".format(totalInvestment)}"
            )
            
            AnalysisCard(
                title = "Ganancia Esperada",
                content = "$${"%.2f".format(expectedRevenue - totalInvestment)}"
            )
            
            AnalysisCard(
                title = "Pérdidas Estimadas",
                // Simulated: Assuming 5% of investment represents damaged/lost on average since we don't have real "expected vs actual" input fields yet.
                content = "Aprox. $${"%.2f".format(totalInvestment * 0.05)} por margen de seguridad."
            )

            AnalysisCard(
                title = "Producto de mayor demanda",
                content = highestDemand?.name ?: "No hay datos suficientes",
                subtitle = highestDemand?.let { "Quedan ${it.quantity} unidades. (Código: ${it.code})" }
            )

            AnalysisCard(
                title = "Producto más ineficiente",
                content = mostInefficient?.name ?: "No hay datos suficientes",
                subtitle = mostInefficient?.let { "Margen de ganancia: $${"%.2f".format(it.sellPrice - it.buyPrice)}" }
            )
        }
    }
}

@Composable
fun AnalysisCard(title: String, content: String, subtitle: String? = null) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = content, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
