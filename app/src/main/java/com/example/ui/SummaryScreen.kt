package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.viewmodel.InventoryViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf

import com.example.ui.theme.getAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(navController: NavController, viewModel: InventoryViewModel) {
    val totalInvestment by viewModel.totalInvestment.collectAsStateWithLifecycle()
    val expectedRevenue by viewModel.expectedRevenue.collectAsStateWithLifecycle()
    val products by viewModel.uiState.collectAsStateWithLifecycle()
    val currency = viewModel.settingsManager.currencySymbol
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val appColors = getAppColors(isDarkMode)

    val backgroundNavy = appColors.backgroundNavy
    val cardNavy = appColors.cardNavy
    val limeGreen = appColors.limeGreen
    val textSilver = appColors.textSilver
    
    val profit = expectedRevenue - totalInvestment

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Inventario", color = textSilver, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = textSilver)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundNavy,
                    titleContentColor = textSilver,
                    navigationIconContentColor = textSilver
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundNavy)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = cardNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Valor Total", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$currency${"%.2f".format(totalInvestment).replace(",", ".")}", color = limeGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = cardNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Ganancia Proyectada", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$currency${"%.2f".format(profit).replace(",", ".")}", color = Color(0xFF00B4DB), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }

            Text("Visualización de Inventario", color = textSilver, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = cardNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    if (products.isEmpty()) {
                        Text("Agrega productos para visualizar el gráfico", color = textSilver.copy(alpha = 0.7f))
                    } else {
                        val sortedProducts = products.sortedBy { it.name }
                        val valueEntries = sortedProducts.mapIndexed { index, product ->
                            FloatEntry(x = index.toFloat(), y = (product.quantity * product.buyPrice).toFloat())
                        }
                        val profitEntries = sortedProducts.mapIndexed { index, product ->
                            FloatEntry(x = index.toFloat(), y = (product.quantity * (product.sellPrice - product.buyPrice)).toFloat())
                        }
                        
                        val chartModel = entryModelOf(valueEntries, profitEntries)
                        
                        Chart(
                            chart = lineChart(
                                lines = listOf(
                                    lineSpec(lineColor = textSilver), // Inversión
                                    lineSpec(lineColor = limeGreen)   // Ganancia
                                )
                            ),
                            model = chartModel,
                            startAxis = rememberStartAxis(
                                label = com.patrykandpatrick.vico.compose.axis.axisLabelComponent(
                                    color = textSilver.copy(alpha = 0.7f),
                                    textSize = 10.sp
                                )
                            ),
                            bottomAxis = rememberBottomAxis(
                                label = com.patrykandpatrick.vico.compose.axis.axisLabelComponent(
                                    color = textSilver.copy(alpha = 0.7f),
                                    textSize = 10.sp
                                )
                            )
                        )
                    }
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(textSilver, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Inversión", color = textSilver, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(limeGreen, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ganancia", color = textSilver, fontSize = 12.sp)
                }
            }
        }
    }
}
