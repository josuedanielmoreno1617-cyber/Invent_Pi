package com.example.ui

import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDataScreen(navController: NavController, viewModel: InventoryViewModel) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cargar Datos") },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Información Básica", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = productNumber, onValueChange = { productNumber = it },
                        label = { Text("Número de producto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = code, onValueChange = { code = it },
                        label = { Text("Código de barras/SKU") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Producto (Nombre)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Descripción del producto") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Cantidades y Precios", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantity, onValueChange = { quantity = it },
                            label = { Text("Existencia") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = location, onValueChange = { location = it },
                            label = { Text("Ubicación") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = unitsPerBox, onValueChange = { unitsPerBox = it },
                            label = { Text("Unidades por caja") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unitsPerBulk, onValueChange = { unitsPerBulk = it },
                            label = { Text("Unidades por bulto") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = buyPrice, onValueChange = { buyPrice = it },
                            label = { Text("Precio de compra") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sellPrice, onValueChange = { sellPrice = it },
                            label = { Text("Precio de venta") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val q = quantity.toIntOrNull() ?: 0
                    val bPrice = buyPrice.toDoubleOrNull() ?: 0.0
                    val sPrice = sellPrice.toDoubleOrNull() ?: 0.0
                    val uBox = unitsPerBox.toIntOrNull() ?: 0
                    val uBulk = unitsPerBulk.toIntOrNull() ?: 0
                    if (name.isNotBlank()) {
                        viewModel.addProduct(
                            Product(
                                productNumber = productNumber,
                                code = code,
                                name = name,
                                description = description,
                                quantity = q,
                                unitsPerBox = uBox,
                                unitsPerBulk = uBulk,
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
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("El nombre del producto es obligatorio")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Guardar Producto", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Función de Excel no disponible en esta versión.")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Cargar archivos Excel", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
