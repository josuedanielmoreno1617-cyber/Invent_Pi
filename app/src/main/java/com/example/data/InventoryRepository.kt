package com.example.data

import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val productDao: ProductDao,
    private val inventoryLogDao: InventoryLogDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allLogs: Flow<List<InventoryLog>> = inventoryLogDao.getAllLogs()

    suspend fun getProductByCode(code: String): Product? = productDao.getProductByCode(code)

    suspend fun insert(product: Product) = productDao.insertProduct(product)
    
    suspend fun updateProducts(products: List<Product>) = productDao.updateProducts(products)
    
    suspend fun deleteById(id: Int) = productDao.deleteProductById(id)

    suspend fun deleteAll() = productDao.deleteAllProducts()
    
    suspend fun deleteAllLogs() = inventoryLogDao.deleteAllLogs()
    
    suspend fun insertLog(log: InventoryLog) = inventoryLogDao.insertLog(log)
    
    suspend fun insertLogs(logs: List<InventoryLog>) = inventoryLogDao.insertLogs(logs)
}
