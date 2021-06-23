package com.erzhan.inventory

import android.app.Application
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.model.data.InventoryDatabase

class InventoryRepository(app: Application) {

    val app: Application
    lateinit var inventory: Inventory
    lateinit var inventoryList: List<Inventory>

    init {
        this.app = app
    }

    suspend fun getAllInventory(): List<Inventory> {
        inventoryList = InventoryDatabase(app).getInventoryDao().getAllInventory()
        return inventoryList
    }

    suspend fun addInventory(inventory: Inventory) {
        InventoryDatabase(app).getInventoryDao().addInventory(inventory)
    }


    suspend fun getInventoryById(inventoryId: Int): Inventory {
        inventory = InventoryDatabase(app).getInventoryDao().getInventoryById(inventoryId)
        return inventory
    }

    suspend fun updateInventory(inventory: Inventory) {
        InventoryDatabase(app).getInventoryDao().updateInventory(inventory)

    }

    suspend fun deleteInventory(inventory: Inventory) {
        InventoryDatabase(app).getInventoryDao().deleteInventory(inventory)
    }

    suspend fun deleteAll() {
        InventoryDatabase(app).getInventoryDao().deleteAll()
    }
}