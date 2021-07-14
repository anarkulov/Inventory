package com.erzhan.inventory.view

import android.content.DialogInterface
import androidx.lifecycle.LiveData
import com.erzhan.inventory.model.data.Inventory
import kotlinx.coroutines.flow.Flow

interface MyContract {

    interface BasicPresenter {
        suspend fun getInventoryById(inventoryId: Int): Inventory
    }

    interface BasicView {

    }

    interface CatalogPresenter {
        fun deleteAllInventories()
        suspend fun getAllInventories() : List<Inventory>
        fun insertDummyData()
        fun start()
    }

    interface CatalogView{
        fun updateDataOnAdd(inventoryList: List<Inventory>)
        fun showAllInventories(inventoryList: List<Inventory>)
    }

    interface DetailPresenter : BasicPresenter{
        fun removeInventory(inventoryId: Int)
    }

    interface DetailView{
        fun showSelectedInventory(inventory: Inventory)
        fun updateDataOnEdit(inventory: Inventory)
        fun onClickEditInventoryButton(inventoryId: Int)
        fun onClickLocation()
    }

    interface EditorPresenter : BasicPresenter{
        fun updateInventory(inventory: Inventory)
        fun addInventory(inventory: Inventory)
    }

    interface EditorView {
        fun showSelectedInventory(inventory: Inventory)
//        fun updateDataOnEdit(inventory: Inventory)
        fun showUnsavedChangesDialog(discardButtonClickListener: DialogInterface.OnClickListener)
        fun saveInventory()
        fun setupSpinner()
        fun chooseImage()
    }
}