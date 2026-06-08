package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.viewmodel.InventoryViewModel
import com.example.R

import com.example.ui.theme.getAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(navController: NavController, viewModel: InventoryViewModel) {
    val totalInvestment by viewModel.totalInvestment.collectAsStateWithLifecycle()
    val expectedRevenue by viewModel.expectedRevenue.collectAsStateWithLifecycle()
    val highestDemand by viewModel.highestDemandProduct.collectAsStateWithLifecycle()
    val mostInefficient by viewModel.mostInefficientProduct.collectAsStateWithLifecycle()
    val currency = viewModel.settingsManager.currencySymbol
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val appColors = getAppColors(isDarkMode)

    val backgroundNavy = appColors.backgroundNavy
    val cardNavy = appColors.cardNavy
    val textSilver = appColors.textSilver
    val limeGreen = appColors.limeGreen
    val accentBlue = Color(0xFF00B4DB)
    val accentRed = Color(0xFFFF4550)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis Automático", color = textSilver, fontWeight = FontWeight.Bold) },
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
            // App Logo
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                // To avoid using AILogo directly in case it's tight to HomeScreen, we use the generated drawable
                Icon(
                    painter = painterResource(id = R.drawable.ai_inventario_logo_v2_1780432286696),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp),
                    tint = Color.Unspecified
                )
            }

            // TEORIA
            SectionTitle(title = "Teoría e Hipótesis", icon = Icons.Default.Info, color = accentBlue)
            Card(
                colors = CardDefaults.cardColors(containerColor = cardNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Con un margen de inversión de $currency${"%.2f".format(totalInvestment)}, la rentabilidad se basa en la rápida rotación de inventarios. Se sugiere un margen de seguridad del 5% frente a posibles pérdidas, ajustando las compras para evitar exceso de stock.",
                    color = textSilver.copy(alpha = 0.8f),
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 22.sp
                )
            }

            // ESTADISTICAS
            SectionTitle(title = "Estadísticas", icon = Icons.Default.PieChart, color = limeGreen)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Inversión",
                    value = "$currency${"%.2f".format(totalInvestment)}",
                    cardNavy = cardNavy,
                    textSilver = textSilver
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Ganancia Neta",
                    value = "$currency${"%.2f".format(expectedRevenue - totalInvestment)}",
                    cardNavy = cardNavy,
                    textSilver = textSilver,
                    valueColor = limeGreen
                )
            }

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Pérdidas Estimadas (Margen Seguridad)",
                value = "Aprox. $currency${"%.2f".format(totalInvestment * 0.05)}",
                cardNavy = cardNavy,
                textSilver = textSilver,
                valueColor = accentRed
            )

            // PATRON DE VENTA
            SectionTitle(title = "Patrón de Venta", icon = Icons.Default.Timeline, color = Color(0xFFFFA726))
            Card(
                colors = CardDefaults.cardColors(containerColor = cardNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Producto más popular:", color = textSilver, fontWeight = FontWeight.Bold)
                    Text(
                        text = highestDemand?.name ?: "No hay datos suficientes", 
                        color = limeGreen,
                        fontSize = 18.sp
                    )
                    Text(text = "Unidades disponibles: ${highestDemand?.quantity ?: 0}", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(text = "Patrón Cíclico Identificado:", color = textSilver, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (highestDemand != null) "Alta rotación de '${highestDemand?.name}'. Alerta de reposición próxima." else "Sin datos históricos.",
                        color = textSilver.copy(alpha = 0.8f)
                    )
                }
            }

            // GRAFICO
            SectionTitle(title = "Gráfico", icon = Icons.Default.ShowChart, color = Color(0xFFAB47BC))
            Card(
                colors = CardDefaults.cardColors(containerColor = cardNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(250.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val products by viewModel.uiState.collectAsStateWithLifecycle()
                    if (products.isEmpty()) {
                        Text("No hay datos para la gráfica", color = textSilver)
                    } else {
                        val sortedProducts = products.sortedBy { it.dateJoined }
                        
                        // We will build a trend of expected profit (sell - buy * quantity) over time
                        val profitEntries = sortedProducts.mapIndexed { index, product ->
                            com.patrykandpatrick.vico.core.entry.FloatEntry(
                                x = index.toFloat(),
                                y = ((product.sellPrice - product.buyPrice) * product.quantity).toFloat()
                            )
                        }
                        
                        val costEntries = sortedProducts.mapIndexed { index, product ->
                            com.patrykandpatrick.vico.core.entry.FloatEntry(
                                x = index.toFloat(),
                                y = (product.buyPrice * product.quantity).toFloat()
                            )
                        }

                        val chartModel = com.patrykandpatrick.vico.core.entry.entryModelOf(costEntries, profitEntries)
                        
                        com.patrykandpatrick.vico.compose.chart.Chart(
                            chart = com.patrykandpatrick.vico.compose.chart.line.lineChart(
                                lines = listOf(
                                    com.patrykandpatrick.vico.compose.chart.line.lineSpec(
                                        lineColor = accentRed // Cost
                                    ),
                                    com.patrykandpatrick.vico.compose.chart.line.lineSpec(
                                        lineColor = limeGreen // Profit
                                    )
                                )
                            ),
                            model = chartModel,
                            startAxis = com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis(
                                label = com.patrykandpatrick.vico.compose.axis.axisLabelComponent(
                                    color = textSilver.copy(alpha = 0.7f),
                                    textSize = 10.sp
                                )
                            ),
                            bottomAxis = com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis(
                                label = com.patrykandpatrick.vico.compose.axis.axisLabelComponent(
                                    color = textSilver.copy(alpha = 0.7f),
                                    textSize = 10.sp
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    cardNavy: Color,
    textSilver: Color,
    valueColor: Color = textSilver
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardNavy),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
