package com.erzhan.inventory.presenter

import android.content.Context
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.model.data.InventoryDatabase
import com.erzhan.inventory.view.MyContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorPresenter(val context: Context) : MyContract.EditorPresenter {

    private val repository: InventoryRepository =
        InventoryRepository(InventoryDatabase(context).getInventoryDao())

    override fun updateInventory(inventory: Inventory) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.updateInventory(inventory)
        }
    }

    override fun addInventory(inventory: Inventory) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.addInventory(inventory)
        }
    }

    override suspend fun getInventoryById(inventoryId: Int): Inventory {
        val inventory = repository.getInventoryById(inventoryId)
        return inventory
    }
}