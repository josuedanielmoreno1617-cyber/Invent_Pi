package com.example.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: InventoryViewModel) {
    val settingsManager = viewModel.settingsManager

    var notifyLowStock by remember { mutableStateOf(settingsManager.notifyLowStock) }
    var notifyPredictions by remember { mutableStateOf(settingsManager.notifyPredictions) }
    var notifyInventoryChanges by remember { mutableStateOf(settingsManager.notifyInventoryChanges) }
    
    var businessData by remember { mutableStateOf(settingsManager.businessData) }
    var storeLocation by remember { mutableStateOf(settingsManager.storeLocation) }
    var notifyPriceChanges by remember { mutableStateOf(settingsManager.notifyPriceChanges) }
    var enableProductImage by remember { mutableStateOf(settingsManager.enableProductImage) }
    var currencySymbol by remember { mutableStateOf(settingsManager.currencySymbol) }

    val backgroundNavy = Color(0xFF0A1F38).copy(alpha = 0.6f)
    val textSilver = Color(0xFFE0E2E6)
    val fieldBackground = Color(0xFFE0E2E6)
    val textDark = Color(0xFF333333)
    val textGray = Color(0xFF666666)
    val limeGreen = Color(0xFF98FB37)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", color = textSilver, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackground,
                unfocusedContainerColor = fieldBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = textDark,
                focusedTextColor = textDark,
                unfocusedTextColor = textDark,
                focusedLabelColor = textGray,
                unfocusedLabelColor = textGray
            )

            // Business Data
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Datos del Negocio", color = textSilver, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                
                TextField(
                    value = businessData,
                    onValueChange = { 
                        businessData = it
                        settingsManager.businessData = it
                    },
                    label = { Text("Nombre / Descripción del negocio") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                
                TextField(
                    value = storeLocation,
                    onValueChange = { 
                        storeLocation = it
                        settingsManager.storeLocation = it
                    },
                    label = { Text("Ubicación del local") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                var expanded by remember { mutableStateOf(false) }
                val currencies = listOf("$", "€", "£", "¥", "MXN", "COP", "PEN", "ARS", "CLP")
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = currencySymbol,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Moneda Base") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = fieldBackground,
                            unfocusedContainerColor = fieldBackground,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(fieldBackground)
                    ) {
                        currencies.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption, color = textDark) },
                                onClick = {
                                    currencySymbol = selectionOption
                                    settingsManager.currencySymbol = selectionOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = textSilver.copy(alpha = 0.2f))

            // Notifications
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Notificaciones", color = textSilver, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                
                SettingSwitchRow(
                    title = "Notificaciones de alerta de inventario",
                    description = "Avisar cuando el stock sea bajo.",
                    checked = notifyLowStock,
                    textColor = textSilver,
                    checkedTrackColor = limeGreen
                ) { 
                    notifyLowStock = it 
                    settingsManager.notifyLowStock = it 
                }

                SettingSwitchRow(
                    title = "Notificaciones de cambio de precio",
                    description = "Alertas cuando se actualice el costo o precio.",
                    checked = notifyPriceChanges,
                    textColor = textSilver,
                    checkedTrackColor = limeGreen
                ) { 
                    notifyPriceChanges = it 
                    settingsManager.notifyPriceChanges = it 
                }
            }

            HorizontalDivider(color = textSilver.copy(alpha = 0.2f))

            // Application Settings
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Ajustes de la Aplicación", color = textSilver, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                
                SettingSwitchRow(
                    title = "Predicción de pérdidas y ganancias",
                    description = "Habilitar análisis inteligente del inventario.",
                    checked = notifyPredictions,
                    textColor = textSilver,
                    checkedTrackColor = limeGreen
                ) { 
                    notifyPredictions = it 
                    settingsManager.notifyPredictions = it 
                }

                SettingSwitchRow(
                    title = "Imagen de producto",
                    description = "Habilitar carga de imágenes para productos.",
                    checked = enableProductImage,
                    textColor = textSilver,
                    checkedTrackColor = limeGreen
                ) { 
                    enableProductImage = it 
                    settingsManager.enableProductImage = it 
                }
            }

            HorizontalDivider(color = textSilver.copy(alpha = 0.2f))

            // Datos y Respaldo
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Datos y Respaldo", color = textSilver, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                
                val context = LocalContext.current
                
                val csvExportLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument("text/csv"),
                    onResult = { uri ->
                        if (uri != null) {
                            viewModel.generateCSVString { csvContent ->
                                try {
                                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                        outputStream.write(csvContent.toByteArray())
                                    }
                                    Toast.makeText(context, "Exportado a CSV exitosamente", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )

                val pdfExportLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument("application/pdf"),
                    onResult = { uri ->
                        if (uri != null) {
                            try {
                                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                    viewModel.generatePDF(outputStream) { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error abriendo archivo: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )

                Button(
                    onClick = {
                        csvExportLauncher.launch("inventario_backup.csv")
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4DB), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Exportar Inventario a CSV", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        pdfExportLauncher.launch("inventario_reporte.pdf")
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAB47BC), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Exportar Reporte a PDF", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = {
                        viewModel.backupData(context) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = limeGreen, contentColor = backgroundNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Crear Copia de Seguridad Local", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.restoreData(context) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = fieldBackground, contentColor = textDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Restaurar Copia de Seguridad", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    textColor: Color,
    checkedTrackColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = textColor, fontWeight = FontWeight.SemiBold)
            Text(
                text = description,
                fontSize = 13.sp,
                color = textColor.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = checkedTrackColor,
                uncheckedThumbColor = textColor.copy(alpha = 0.6f),
                uncheckedTrackColor = textColor.copy(alpha = 0.2f)
            )
        )
    }
}
