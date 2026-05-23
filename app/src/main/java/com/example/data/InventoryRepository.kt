package com.example.data

import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val productDao: ProductDao) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    suspend fun insert(product: Product) = productDao.insertProduct(product)
    
    suspend fun deleteById(id: Int) = productDao.deleteProductById(id)
}
