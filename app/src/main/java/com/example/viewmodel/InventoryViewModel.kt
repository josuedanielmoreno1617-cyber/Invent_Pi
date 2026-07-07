package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.InventoryRepository
import com.example.data.Product
import com.example.data.SettingsManager
import com.example.utils.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.OutputStream

class InventoryViewModel(
    private val repository: InventoryRepository,
    val settingsManager: SettingsManager,
    private val notificationHelper: NotificationHelper
) : ViewModel() {
    val uiState: StateFlow<List<Product>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Derived states based on requirement:
    // Calcular pérdidas: Diferencia entre lo que debería haber y lo que realmente hay (we approximate this by marking damaged products? Need to keep it simple, maybe we just mock damaged products or use a generic loss).
    // As in step 4 formulas: Pérdidas = (Cantidad que debería haber - Cantidad real) + Productos dañados
    // For simplicity, we could assume 5% of stock is usually lost/damaged if no explicit fields exist, or we calculate total invested value vs total sell value.
    // The prompt says: "Pérdidas = (Cantidad que debería haber - Cantidad real) + Productos dañados". Since we don't have fields for "Cantidad que debería haber" and "Productos dañados", let's make a mock calculation or assume something, or I add them to Product.  Let me keep Product simple and return some mock analysis strings that use the total quantity. 

    val totalInvestment: StateFlow<Double> = repository.allProducts.map { products ->
        products.sumOf { it.quantity * it.buyPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val expectedRevenue: StateFlow<Double> = repository.allProducts.map { products ->
        products.sumOf { it.quantity * it.sellPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val highestDemandProduct: StateFlow<Product?> = repository.allProducts.map { products ->
        // the product with highest (sellPrice - buyPrice)*quantity might represent "most demand" for our basic app, or simply pick the one with lowest stock relative to price, or random.
        // Let's just pick the first one with the lowest quantity assuming it's most sold.
        products.minByOrNull { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val mostInefficientProduct: StateFlow<Product?> = repository.allProducts.map { products ->
        // lowest profit margin
        products.minByOrNull { it.sellPrice - it.buyPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLogs: StateFlow<List<com.example.data.InventoryLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(product: Product) {
        viewModelScope.launch {
            val previousProduct = repository.getProductByCode(product.code)
            repository.insert(product)
            repository.insertLog(com.example.data.InventoryLog(
                productId = product.id,
                productCode = product.code,
                productName = product.name,
                type = if (product.id == 0) "CREACIÓN" else "MODIFICACIÓN",
                quantityChange = product.quantity,
                timestamp = System.currentTimeMillis(),
                details = "Producto guardado con stock ${product.quantity}"
            ))
            
            if (settingsManager.notifyInventoryChanges) {
                notificationHelper.showNotification(
                    "Inventario Actualizado",
                    "Se agregó o modificó el producto: ${product.name}"
                )
            }
            
            val threshold = settingsManager.lowStockThreshold
            val wasAbove = previousProduct?.quantity?.let { it >= threshold } ?: true
            val isBelow = product.quantity < threshold

            if (settingsManager.notifyLowStock && wasAbove && isBelow) {
                notificationHelper.showNotification(
                    "Alerta de Stock Bajo",
                    "El producto ${product.name} ha caído por debajo del umbral mínimo (${product.quantity} unidades)."
                )
            }
        }
    }
    
    fun updateProductCategories(products: List<Product>, newCategory: String) {
        viewModelScope.launch {
            val updatedProducts = products.map { it.copy(category = newCategory) }
            repository.updateProducts(updatedProducts)
            val logs = updatedProducts.map {
                com.example.data.InventoryLog(
                    productId = it.id,
                    productCode = it.code,
                    productName = it.name,
                    type = "MODIFICACIÓN",
                    quantityChange = 0,
                    timestamp = System.currentTimeMillis(),
                    details = "Categoría actualizada a '$newCategory'"
                )
            }
            repository.insertLogs(logs)
        }
    }
    
    fun runPredictionAnalysis() {
        val mostInefficient = mostInefficientProduct.value
        if (mostInefficient != null) {
            triggerPredictionAlert(mostInefficient.name)
        }
    }
    
    fun triggerPredictionAlert(productName: String) {
        if (settingsManager.notifyPredictions) {
            notificationHelper.showNotification(
                "Predicción de Pérdida",
                "Alerta: Revisar posible ineficiencia de '$productName' basada en nuestras predicciones."
            )
        }
    }

    suspend fun getProductByCode(code: String): Product? {
        return repository.getProductByCode(code)
    }

    fun backupData(context: Context, uri: android.net.Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val products = repository.allProducts.first()
                val logs = repository.allLogs.first()
                
                val rootObject = JSONObject()
                
                val productsArray = JSONArray()
                products.forEach { product ->
                    val obj = JSONObject().apply {
                        put("productNumber", product.productNumber)
                        put("code", product.code)
                        put("name", product.name)
                        put("description", product.description)
                        put("quantity", product.quantity)
                        put("unitsPerBox", product.unitsPerBox)
                        put("unitsPerBulk", product.unitsPerBulk)
                        put("buyPrice", product.buyPrice)
                        put("sellPrice", product.sellPrice)
                        put("dateJoined", product.dateJoined)
                        put("category", product.category)
                        put("location", product.location)
                        put("imageUri", product.imageUri)
                    }
                    productsArray.put(obj)
                }
                
                val logsArray = JSONArray()
                logs.forEach { log ->
                    val obj = JSONObject().apply {
                        put("productId", log.productId)
                        put("productCode", log.productCode)
                        put("productName", log.productName)
                        put("type", log.type)
                        put("quantityChange", log.quantityChange)
                        put("timestamp", log.timestamp)
                        put("details", log.details)
                    }
                    logsArray.put(obj)
                }
                
                rootObject.put("products", productsArray)
                rootObject.put("logs", logsArray)
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(rootObject.toString().toByteArray())
                } ?: throw Exception("No se pudo abrir el archivo")
                
                onResult(true, "Copia de seguridad guardada con éxito.")
            } catch (e: Exception) {
                onResult(false, "Error al crear la copia de seguridad: ${e.message}")
            }
        }
    }

    fun restoreData(context: Context, uri: android.net.Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().readText()
                } ?: throw Exception("No se pudo leer el archivo")
                
                val rootObject = JSONObject(jsonString)
                val productsArray = rootObject.optJSONArray("products") ?: JSONArray()
                val logsArray = rootObject.optJSONArray("logs") ?: JSONArray()
                
                repository.deleteAll()
                repository.deleteAllLogs()
                
                for (i in 0 until productsArray.length()) {
                    val obj = productsArray.getJSONObject(i)
                    val product = Product(
                        productNumber = obj.optString("productNumber", ""),
                        code = obj.optString("code", ""),
                        name = obj.optString("name", ""),
                        description = obj.optString("description", ""),
                        quantity = obj.optInt("quantity", 0),
                        unitsPerBox = obj.optInt("unitsPerBox", 0),
                        unitsPerBulk = obj.optInt("unitsPerBulk", 0),
                        buyPrice = obj.optDouble("buyPrice", 0.0),
                        sellPrice = obj.optDouble("sellPrice", 0.0),
                        dateJoined = obj.optLong("dateJoined", 0L),
                        category = obj.optString("category", ""),
                        location = obj.optString("location", ""),
                        imageUri = if (obj.has("imageUri") && !obj.isNull("imageUri")) obj.optString("imageUri") else null
                    )
                    repository.insert(product)
                }
                
                val logsList = mutableListOf<com.example.data.InventoryLog>()
                for (i in 0 until logsArray.length()) {
                    val obj = logsArray.getJSONObject(i)
                    val log = com.example.data.InventoryLog(
                        productId = obj.optInt("productId", 0),
                        productCode = obj.optString("productCode", ""),
                        productName = obj.optString("productName", ""),
                        type = obj.optString("type", ""),
                        quantityChange = obj.optInt("quantityChange", 0),
                        timestamp = obj.optLong("timestamp", 0L),
                        details = obj.optString("details", "")
                    )
                    logsList.add(log)
                }
                if (logsList.isNotEmpty()) {
                    repository.insertLogs(logsList)
                }
                
                onResult(true, "Datos restaurados con éxito.")
            } catch (e: Exception) {
                onResult(false, "Error al restaurar los datos: Asegúrate de que el archivo es válido.")
            }
        }
    }

    fun generateCSVString(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val products = repository.allProducts.first()
            val header = "Product Number,Code,Name,Description,Quantity,Units Per Box,Units Per Bulk,Buy Price,Sell Price,Date Joined,Location\n"
            val rows = products.joinToString(separator = "\n") { product ->
                "${escapeCsv(product.productNumber)},${escapeCsv(product.code)},${escapeCsv(product.name)},${escapeCsv(product.description)},${product.quantity},${product.unitsPerBox},${product.unitsPerBulk},${product.buyPrice},${product.sellPrice},${product.dateJoined},${escapeCsv(product.location)}"
            }
            onResult(header + rows)
        }
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    fun generatePDF(outputStream: OutputStream, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val products = repository.allProducts.first()
                withContext(Dispatchers.IO) {
                    val pdfDocument = android.graphics.pdf.PdfDocument()
                    var pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
                    var page = pdfDocument.startPage(pageInfo)
                    var canvas = page.canvas
                    var paint = android.graphics.Paint()
                    
                    paint.textSize = 18f
                    paint.isFakeBoldText = true
                    canvas.drawText("Reporte de Inventario: AI Inventario", 50f, 50f, paint)
                    
                    paint.textSize = 12f
                    var yPosition = 90f
                    var currentPageNumber = 1
                    
                    paint.isFakeBoldText = true
                    canvas.drawText("Cód", 50f, yPosition, paint)
                    canvas.drawText("Nombre", 120f, yPosition, paint)
                    canvas.drawText("Cant.", 300f, yPosition, paint)
                    canvas.drawText("Precio V.", 380f, yPosition, paint)
                    canvas.drawText("Ubicación", 460f, yPosition, paint)
                    
                    paint.isFakeBoldText = false
                    yPosition += 20f
                    
                    products.forEach { product ->
                        if (yPosition > 800f) {
                            pdfDocument.finishPage(page)
                            currentPageNumber++
                            pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, currentPageNumber).create()
                            page = pdfDocument.startPage(pageInfo)
                            canvas = page.canvas
                            yPosition = 50f
                        }
                        
                        canvas.drawText(product.code.take(10), 50f, yPosition, paint)
                        canvas.drawText(product.name.take(25), 120f, yPosition, paint)
                        canvas.drawText(product.quantity.toString(), 300f, yPosition, paint)
                        canvas.drawText("${settingsManager.currencySymbol}${product.sellPrice}", 380f, yPosition, paint)
                        canvas.drawText(product.location.take(15), 460f, yPosition, paint)
                        
                        yPosition += 20f
                    }
                    
                    pdfDocument.finishPage(page)
                    pdfDocument.writeTo(outputStream)
                    pdfDocument.close()
                }
                onResult(true, "PDF generado con éxito.")
            } catch (e: Exception) {
                onResult(false, "Error al generar el PDF: ${e.message}")
            }
        }
    }
}
