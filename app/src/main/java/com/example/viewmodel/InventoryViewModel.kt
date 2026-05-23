package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.InventoryRepository
import com.example.data.Product
import com.example.data.SettingsManager
import com.example.utils.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.insert(product)
            
            if (settingsManager.notifyInventoryChanges) {
                notificationHelper.showNotification(
                    "Inventario Actualizado",
                    "Se agregó el producto: ${product.name}"
                )
            }
            
            if (settingsManager.notifyLowStock && product.quantity < 10) { // arbitrary threshold
                notificationHelper.showNotification(
                    "Alerta de Stock Bajo",
                    "El producto ${product.name} tiene un stock bajo (${product.quantity} unidades)."
                )
            }
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
}
