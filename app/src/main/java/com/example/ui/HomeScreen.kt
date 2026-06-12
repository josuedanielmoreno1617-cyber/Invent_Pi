package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

import com.example.viewmodel.InventoryViewModel
import com.example.data.Product
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.compose.material.icons.filled.QrCodeScanner

import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(navController: NavController, viewModel: InventoryViewModel) {
    val context = LocalContext.current
    var isMenuOpen by remember { mutableStateOf(false) }
    var scannedProduct by remember { mutableStateOf<Product?>(null) }
    var showScanDialog by remember { mutableStateOf(false) }
    var lastScannedCode by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val allProducts by viewModel.uiState.collectAsStateWithLifecycle()
    
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedProductIds by remember { mutableStateOf(setOf<Int>()) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var categoryInput by remember { mutableStateOf("") }
    
    var showTutorial by remember { mutableStateOf(!viewModel.settingsManager.hasSeenTutorial) }
    
    val filteredProducts = remember(searchQuery, allProducts) {
        if (searchQuery.isBlank()) {
            allProducts
        } else {
            allProducts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.code.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val scope = rememberCoroutineScope()
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            lastScannedCode = result.contents
            scope.launch {
                scannedProduct = viewModel.getProductByCode(result.contents)
                showScanDialog = true
            }
        }
    }

    val backgroundNavy = Color(0xFF0A1F38).copy(alpha = 0.6f)
    val textSilver = Color(0xFFE0E2E6)
    val limeGreen = Color(0xFF98FB37)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundNavy)
    ) {
        // Main Screen Content
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Izquierda: Logo miniatura + texto
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AILogo(
                        modifier = Modifier, 
                        showText = false, 
                        logoSize = 40.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "AI INVENTARIO",
                        color = textSilver,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Derecha: Icono de menu
                IconButton(onClick = { isMenuOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = textSilver,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Contenido central
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Dashboard Section
                val totalInvestment by viewModel.totalInvestment.collectAsStateWithLifecycle()
                val expectedRevenue by viewModel.expectedRevenue.collectAsStateWithLifecycle()
                val profit = expectedRevenue - totalInvestment
                val lowStockCount = allProducts.count { it.quantity < 10 }
                val currency = viewModel.settingsManager.currencySymbol
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF112B4A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Valor Total", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$currency${"%.2f".format(totalInvestment).replace(",", ".")}", color = limeGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF112B4A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Ganancia Mes", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$currency${"%.2f".format(profit).replace(",", ".")}", color = Color(0xFF00B4DB), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF112B4A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Por Agotar", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$lowStockCount arts", color = Color(0xFFFF4550), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                val accentBlue = Color(0xFF00B4DB)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Buscar producto...", color = textSilver.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = textSilver) },
                        trailingIcon = { 
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Borrar", tint = textSilver)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentBlue,
                            unfocusedBorderColor = textSilver.copy(alpha = 0.3f),
                            focusedTextColor = textSilver,
                            unfocusedTextColor = textSilver,
                            cursorColor = accentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { 
                            isSelectionMode = !isSelectionMode
                            if (!isSelectionMode) selectedProductIds = emptySet()
                        },
                        modifier = Modifier.background(if (isSelectionMode) limeGreen else Color.Transparent, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Checklist, contentDescription = "Seleccionar", tint = if (isSelectionMode) backgroundNavy else textSilver)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isEmpty()) "El inventario está vacío" else "No se encontraron productos",
                            color = textSilver.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(bottom = 100.dp), // For FAB
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            val isSelected = selectedProductIds.contains(product.id)
                            ProductItem(
                                product = product,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                currencySymbol = viewModel.settingsManager.currencySymbol,
                                onClick = {
                                    if (isSelectionMode) {
                                        selectedProductIds = if (isSelected) {
                                            selectedProductIds - product.id
                                        } else {
                                            selectedProductIds + product.id
                                        }
                                    } else {
                                        // View / Edit logic
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (isSelectionMode && selectedProductIds.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { showCategoryDialog = true },
                    containerColor = Color(0xFF00B4DB),
                    contentColor = Color.White
                ) {
                    Text("Asignar Categoría (${selectedProductIds.size})")
                }
            }
            
            FloatingActionButton(
                onClick = { scanLauncher.launch(ScanOptions()) },
                containerColor = limeGreen,
                contentColor = backgroundNavy
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear Código")
            }
        }

        if (showCategoryDialog) {
            AlertDialog(
                onDismissRequest = { showCategoryDialog = false },
                title = { Text("Asignar Categoría", color = textSilver, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nueva Categoría", color = textSilver.copy(0.7f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textSilver,
                            unfocusedTextColor = textSilver,
                            focusedBorderColor = limeGreen,
                            focusedLabelColor = limeGreen,
                            unfocusedBorderColor = textSilver.copy(0.3f)
                        ),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val selectedProducts = allProducts.filter { selectedProductIds.contains(it.id) }
                        viewModel.updateProductCategories(selectedProducts, categoryInput)
                        showCategoryDialog = false
                        isSelectionMode = false
                        selectedProductIds = emptySet()
                        categoryInput = ""
                    }, colors = ButtonDefaults.buttonColors(containerColor = limeGreen)) {
                        Text("Guardar", color = backgroundNavy, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCategoryDialog = false }) {
                        Text("Cancelar", color = textSilver)
                    }
                },
                containerColor = Color(0xFF112B4A)
            )
        }

        if (showTutorial) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .zIndex(100f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "¡Bienvenido a AI Inventario!",
                        color = limeGreen,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Aquí tienes una guía rápida de las funciones principales:",
                        color = textSilver,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF112B4A), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = limeGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Menú superior izquierdo para navegar a Análisis, Predicciones, Agregar Productos e Historial.", color = textSilver, fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = limeGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Busca productos o usa la selección para asignar categorías en lote.", color = textSilver, fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = limeGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Botón flotante inferior para escanear códigos de barras.", color = textSilver, fontSize = 14.sp)
                        }
                    }

                    Button(
                        onClick = {
                            showTutorial = false
                            viewModel.settingsManager.hasSeenTutorial = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = limeGreen),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                    ) {
                        Text("¡Entendido!", color = backgroundNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        if (showScanDialog) {
            AlertDialog(
                onDismissRequest = { showScanDialog = false },
                title = { Text(if (scannedProduct != null) "Producto Encontrado" else "Producto No Encontrado", color = textSilver, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Código: $lastScannedCode", color = limeGreen, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (scannedProduct != null) {
                            Text("Nombre: ${scannedProduct?.name}", color = textSilver)
                            Text("Stock: ${scannedProduct?.quantity}", color = textSilver)
                            Text("Precio Venta: ${viewModel.settingsManager.currencySymbol}${scannedProduct?.sellPrice}", color = textSilver)
                            Text("Ubicación: ${scannedProduct?.location}", color = textSilver)
                        } else {
                            Text("No tenemos este producto registrado en el inventario.", color = textSilver)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showScanDialog = false
                        navController.navigate("add")
                    }, colors = ButtonDefaults.buttonColors(containerColor = if (scannedProduct != null) Color(0xFF00B4DB) else limeGreen)) {
                        Text(if (scannedProduct != null) "Editar / Actualizar" else "Agregar Producto", color = backgroundNavy, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showScanDialog = false }) {
                        Text("Cerrar", color = textSilver)
                    }
                },
                containerColor = backgroundNavy
            )
        }
        
        // Custom Drawer Overlay Layer
        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isMenuOpen = false }
                    .zIndex(1f)
            )
        }
        
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, 
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }, 
                animationSpec = tween(300)
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .zIndex(2f)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp),
                color = backgroundNavy.copy(alpha = 0.7f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Close button
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        IconButton(onClick = { isMenuOpen = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar menu",
                                tint = textSilver,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Menu Items
                    DrawerItem(
                        title = "Cargar Datos",
                        description = "Agrega nuevos productos al inventario",
                        icon = Icons.Default.Add,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFFFF4550), Color(0xFFFF4081)))
                    ) {
                        isMenuOpen = false
                        navController.navigate("add")
                    }
                    
                    DrawerItem(
                        title = "Ver Análisis",
                        description = "Reportes y estadísticas",
                        icon = Icons.Default.BarChart,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFF00B4DB), Color(0xFF0083B0)))
                    ) {
                        isMenuOpen = false
                        navController.navigate("analysis")
                    }
                    
                    DrawerItem(
                        title = "Resumen Gráfico",
                        description = "Simulación económica visual",
                        icon = Icons.Default.Timeline,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFF26C6DA), Color(0xFF0097A7)))
                    ) {
                        isMenuOpen = false
                        navController.navigate("summary")
                    }
                    
                    DrawerItem(
                        title = "Predicciones",
                        description = "Análisis IA y alertas de stock",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFF8C9EFF), Color(0xFF673AB7)))
                    ) {
                        isMenuOpen = false
                        navController.navigate("prediction")
                    }
                    
                    DrawerItem(
                        title = "Facturas",
                        description = "Gestión de facturas y recibos",
                        icon = Icons.Default.Receipt,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFFFFB74D), Color(0xFFFF9800)))
                    ) {
                        isMenuOpen = false
                        navController.navigate("invoices")
                    }
                    
                    DrawerItem(
                        title = "Ubicación",
                        description = "Ubicación del local",
                        icon = Icons.Default.LocationOn,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF388E3C)))
                    ) {
                        isMenuOpen = false
                        val location = viewModel.settingsManager.storeLocation.ifBlank { "Ciudad" }
                        val uri = Uri.parse("geo:0,0?q=${Uri.encode(location)}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    }
                    
                    DrawerItem(
                        title = "Historial",
                        description = "Movimientos del inventario",
                        icon = Icons.Default.History,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFFAB47BC), Color(0xFF8E24AA)))
                    ) {
                        isMenuOpen = false
                        navController.navigate("history")
                    }
                    
                    DrawerItem(
                        title = "Configuración",
                        description = "Preferencias y notificaciones",
                        icon = Icons.Default.Settings,
                        tint = textSilver,
                        iconBackground = Brush.linearGradient(listOf(Color(0xFF9E9E9E), Color(0xFF616161)))
                    ) {
                        isMenuOpen = false
                        navController.navigate("settings")
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    HorizontalDivider(color = textSilver.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI Inventario - Versión 15.0",
                        color = textSilver.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    title: String,
    description: String,
    icon: ImageVector,
    tint: Color,
    iconBackground: Brush,
    onClick: () -> Unit
) {
    // Just a clean clickable row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconBackground, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = title,
                color = tint, 
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = tint.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    currencySymbol: String,
    onClick: () -> Unit
) {
    val textSilver = Color(0xFFE0E2E6)
    val limeGreen = Color(0xFF98FB37)
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF1E4C80) else Color(0xFF112B4A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                androidx.compose.material3.Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = limeGreen,
                        uncheckedColor = textSilver
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, color = textSilver, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Código: ${product.code}", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
                if (product.category.isNotEmpty()) {
                    Text(text = "Categoría: ${product.category}", color = textSilver.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                val totalProfit = (product.sellPrice - product.buyPrice) * product.quantity
                val isProfit = totalProfit >= 0
                val profitColor = if (isProfit) limeGreen else Color(0xFFFF4550)
                Text(
                    text = "Beneficio esperado: ${(if (isProfit) "+" else "")}${currencySymbol}${"%.2f".format(totalProfit).replace(",", ".")}", 
                    color = profitColor, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${currencySymbol}${product.sellPrice}", color = limeGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Stock: ${product.quantity}", color = textSilver, fontSize = 14.sp)
            }
        }
    }
}
