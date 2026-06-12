package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(navController: NavController, viewModel: InventoryViewModel) {
    val settingsManager = viewModel.settingsManager
    var companyName by remember { mutableStateOf(settingsManager.companyName) }
    var rucNumber by remember { mutableStateOf(settingsManager.rucNumber) }
    var storeLocation by remember { mutableStateOf(settingsManager.storeLocation) }
    
    val backgroundNavy = Color(0xFF0A1F38).copy(alpha = 0.6f)
    val cardNavy = Color(0xFF112B4A)
    val textSilver = Color(0xFFE0E2E6)
    val fieldBackground = Color(0xFFE0E2E6)
    val textDark = Color(0xFF333333)
    val limeGreen = Color(0xFF98FB37)
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = fieldBackground,
        unfocusedContainerColor = fieldBackground,
        focusedTextColor = textDark,
        unfocusedTextColor = textDark,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generar Factura", color = textSilver, fontWeight = FontWeight.Bold) },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundNavy)
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Datos de la Empresa", color = textSilver, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    
                    TextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Nombre de la Empresa") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = textDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors
                    )
                    
                    TextField(
                        value = rucNumber,
                        onValueChange = { rucNumber = it },
                        label = { Text("Número de RUC") },
                        leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = textDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors
                    )
                    
                    TextField(
                        value = storeLocation,
                        onValueChange = { storeLocation = it },
                        label = { Text("Ubicación del Local") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = textDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors
                    )
                }
            }
            
            Button(
                onClick = {
                    // Save to settings
                    settingsManager.companyName = companyName
                    settingsManager.rucNumber = rucNumber
                    settingsManager.storeLocation = storeLocation
                    
                    val currentInvoiceCount = settingsManager.invoiceCount
                    val invoiceNumber = "F" + currentInvoiceCount.toString().padStart(6, '0') // F000001
                    settingsManager.invoiceCount = currentInvoiceCount + 1
                    
                    scope.launch {
                        snackbarHostState.showSnackbar("Factura $invoiceNumber generada exitosamente")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = limeGreen)
            ) {
                Text("Generar Factura", color = Color(0xFF112B4A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
