package com.erzhan.inventory.presenter.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.view.editor.EditorContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorPresenter(val app: Application) : AndroidViewModel(app), EditorContract.Presenter{

    private val repository: InventoryRepository = InventoryRepository(app)

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

    override fun getInventoryById(inventoryId: Int): Inventory {
        var inventory: Inventory? = null
        CoroutineScope(Dispatchers.Main).launch {
            inventory = repository.getInventoryById(inventoryId)
        }
        return inventory!!
    }
}