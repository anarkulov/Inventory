package com.erzhan.inventory

import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.model.data.InventoryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryRepository(private val inventoryDao: InventoryDao) {

    private lateinit var inventory: Inventory
    private lateinit var inventoryList: List<Inventory>

    suspend fun getAllInventories(): List<Inventory> {
        inventoryList = inventoryDao.getAllInventory()
        return inventoryList
    }

    suspend fun addInventory(inventory: Inventory) {
        inventoryDao.addInventory(inventory)
    }


    suspend fun getInventoryById(inventoryId: Int): Inventory {
        inventory = inventoryDao.getInventoryById(inventoryId)
        return inventory
    }

    suspend fun updateInventory(inventory: Inventory) {
        inventoryDao.updateInventory(inventory)
    }

    suspend fun deleteInventory(inventory: Inventory) {
        inventoryDao.deleteInventory(inventory)
    }

    suspend fun deleteAll() {
        inventoryDao.deleteAll()
    }
}