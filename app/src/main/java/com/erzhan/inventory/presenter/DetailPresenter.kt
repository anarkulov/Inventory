package com.erzhan.inventory.presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.view.MyContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailPresenter(private val repository: InventoryRepository, context: Context) : MyContract.DetailPresenter {

    private val view: MyContract.DetailView = context as MyContract.DetailView

    override fun removeInventory(inventoryId: Int) {
        if (inventoryId == -1) {
            Log.v("Log presenter", "Invalid inventory id")
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val inventory = repository.getInventoryById(inventoryId)
                repository.deleteInventory(inventory)
            }
        }
    }

    override suspend fun getInventoryById(inventoryId: Int): Inventory {
        val inventory = repository.getInventoryById(inventoryId)
        return inventory
    }
}