package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.Product
import com.example.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.activity.compose.rememberLauncherForActivityResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

import com.example.ui.theme.getAppColors
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDataScreen(navController: NavController, viewModel: InventoryViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val appColors = getAppColors(isDarkMode)
    
    var productNumber by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitsPerBox by remember { mutableStateOf("") }
    var unitsPerBulk by remember { mutableStateOf("") }
    var buyPrice by remember { mutableStateOf("") }
    var sellPrice by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            code = result.contents
            scope.launch {
                val existingProduct = viewModel.getProductByCode(result.contents)
                if (existingProduct != null) {
                    productNumber = existingProduct.productNumber
                    name = existingProduct.name
                    description = existingProduct.description
                    quantity = existingProduct.quantity.toString()
                    unitsPerBox = existingProduct.unitsPerBox.toString()
                    unitsPerBulk = existingProduct.unitsPerBulk.toString()
                    buyPrice = existingProduct.buyPrice.toString()
                    sellPrice = existingProduct.sellPrice.toString()
                    location = existingProduct.location
                    snackbarHostState.showSnackbar("Producto encontrado y cargado.")
                } else {
                    snackbarHostState.showSnackbar("Código escaneado. Ingresa los datos del nuevo producto.")
                }
            }
        }
    }

    val backgroundNavy = appColors.backgroundNavy
    val cardNavy = appColors.cardNavy
    val textSilver = appColors.textSilver
    val limeGreen = appColors.limeGreen

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = limeGreen,
        unfocusedBorderColor = textSilver,
        focusedTextColor = textSilver,
        unfocusedTextColor = textSilver,
        focusedLabelColor = limeGreen,
        unfocusedLabelColor = textSilver,
        cursorColor = limeGreen
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cargar Datos", color = textSilver) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = cardNavy)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Información Básica", style = MaterialTheme.typography.titleMedium, color = limeGreen)
                    OutlinedTextField(
                        value = productNumber, onValueChange = { productNumber = it },
                        label = { Text("Número de producto") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = code, onValueChange = { code = it },
                        label = { Text("Código de barras/SKU") },
                        trailingIcon = {
                            IconButton(onClick = { scanLauncher.launch(ScanOptions()) }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear", tint = textSilver)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Producto (Nombre)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Descripción del producto") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = textFieldColors
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = cardNavy)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Cantidades y Precios", style = MaterialTheme.typography.titleMedium, color = limeGreen)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantity, onValueChange = { quantity = it },
                            label = { Text("Existencia") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = location, onValueChange = { location = it },
                            label = { Text("Ubicación") },
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = unitsPerBox, onValueChange = { unitsPerBox = it },
                            label = { Text("Unidades por caja") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = unitsPerBulk, onValueChange = { unitsPerBulk = it },
                            label = { Text("Unidades por bulto") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = buyPrice, onValueChange = { buyPrice = it },
                            label = { Text("Precio de compra") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = sellPrice, onValueChange = { sellPrice = it },
                            label = { Text("Precio de venta") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val q = quantity.toIntOrNull()
                    val bPrice = buyPrice.toDoubleOrNull()
                    val sPrice = sellPrice.toDoubleOrNull()
                    val uBox = unitsPerBox.toIntOrNull()
                    val uBulk = unitsPerBulk.toIntOrNull()
                    
                    if (name.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("El nombre del producto es obligatorio") }
                    } else if (q == null || q < 0) {
                        scope.launch { snackbarHostState.showSnackbar("Por favor ingresa una cantidad válida (entero positivo)") }
                    } else if (bPrice == null || bPrice < 0) {
                        scope.launch { snackbarHostState.showSnackbar("Por favor ingresa un precio de compra válido") }
                    } else if (sPrice == null || sPrice < 0) {
                        scope.launch { snackbarHostState.showSnackbar("Por favor ingresa un precio de venta válido") }
                    } else if (unitsPerBox.isNotBlank() && (uBox == null || uBox < 0)) {
                        scope.launch { snackbarHostState.showSnackbar("Las unidades por caja deben ser un entero positivo") }
                    } else if (unitsPerBulk.isNotBlank() && (uBulk == null || uBulk < 0)) {
                        scope.launch { snackbarHostState.showSnackbar("Las unidades por bulto deben ser un entero positivo") }
                    } else {
                        viewModel.addProduct(
                            Product(
                                productNumber = productNumber,
                                code = code,
                                name = name,
                                description = description,
                                quantity = q,
                                unitsPerBox = uBox ?: 0,
                                unitsPerBulk = uBulk ?: 0,
                                buyPrice = bPrice,
                                sellPrice = sPrice,
                                dateJoined = System.currentTimeMillis(),
                                location = location
                            )
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Producto guardado correctamente")
                            productNumber = ""
                            code = ""
                            name = ""
                            description = ""
                            quantity = ""
                            unitsPerBox = ""
                            unitsPerBulk = ""
                            buyPrice = ""
                            sellPrice = ""
                            location = ""
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = limeGreen)
            ) {
                Text("Guardar Producto", style = MaterialTheme.typography.titleMedium, color = cardNavy)
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Función de Excel no disponible en esta versión.")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = limeGreen)
            ) {
                Text("Cargar archivos Excel", style = MaterialTheme.typography.titleMedium, color = limeGreen)
            }
        }
    }
}
