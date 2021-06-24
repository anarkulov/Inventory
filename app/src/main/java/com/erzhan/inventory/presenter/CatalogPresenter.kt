package com.erzhan.inventory.presenter

import android.content.Context
import android.graphics.BitmapFactory
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.view.MyContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatalogPresenter(private val repository: InventoryRepository, val context: Context) :
    MyContract.CatalogPresenter {

    private lateinit var inventoryList: List<Inventory>
    private val view: MyContract.CatalogView = context as MyContract.CatalogView

    override suspend fun getAllInventories(): List<Inventory> {
        inventoryList = repository.getAllInventories()
        return inventoryList
    }

    override fun insertDummyData() {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapImage =
                BitmapFactory.decodeResource(context.resources, R.drawable.image_placeholder)
            val newInventoryDummyData = Inventory(
                "Dummy Title", 100, Inventory.Entry.CURRENCY_DOLLAR,
                2, "Bishkek", "D inc",
                "This is the sample data for this item", bitmapImage
            )
            repository.addInventory(newInventoryDummyData)
            view.updateDataOnAdd(repository.getAllInventories())
        }
    }

    override fun start() {
        CoroutineScope(Dispatchers.Main).launch {
            view.showAllInventories(repository.getAllInventories())
        }
    }

    override fun deleteAllInventories() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.deleteAll()
            view.updateDataOnAdd(repository.getAllInventories())
        }
    }
}