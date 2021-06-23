package com.erzhan.inventory.presenter.detail

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.view.detail.DetailContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailPresenter(val app: Application) : AndroidViewModel(app), DetailContract.Presenter {

    private val repository: InventoryRepository = InventoryRepository(app)

    override fun removeInventory(inventoryId: Int) {
        if (inventoryId == -1) {
            Toast.makeText(
                app, app.getString(R.string.editor_delete_pet_failed),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val inventory = repository.getInventoryById(inventoryId)
                repository.deleteInventory(inventory)
            }
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