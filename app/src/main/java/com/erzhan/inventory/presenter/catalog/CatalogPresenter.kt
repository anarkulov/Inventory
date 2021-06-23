package com.erzhan.inventory.presenter.catalog

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.view.catalog.CatalogContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CatalogPresenter(app: Application) : AndroidViewModel(app), CatalogContract.Presenter{

    private lateinit var inventoryList: List<Inventory>

    private val repository: InventoryRepository = InventoryRepository(app)

    override fun getAllInventories(): List<Inventory> {
        CoroutineScope(Dispatchers.Main).launch {
            inventoryList = repository.getAllInventory()
        }

        return inventoryList
    }

    override fun insertDummyData() {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapImage = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.image_placeholder)
            val newInventoryDummyData = Inventory(
                "Dummy Title", 100, Inventory.Entry.CURRENCY_DOLLAR,
                2, "Bishkek", "D inc",
                "This is the sample data for this item", bitmapImage
            )
            repository.addInventory(newInventoryDummyData)
        }

    }

    override fun deleteAllInventories() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.deleteAll()
        }
    }
}