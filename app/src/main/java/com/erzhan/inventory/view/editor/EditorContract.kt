package com.erzhan.inventory.view.editor

import com.erzhan.inventory.model.data.Inventory

interface EditorContract {

    interface Presenter {
        fun updateInventory(Inventory: Inventory)
        fun addInventory(inventory: Inventory)
        fun getInventoryById(inventoryId: Int): Inventory
    }

    interface View {
        fun showSelectedInventory(inventory: Inventory)
        fun updateDataOnEdit(inventory: Inventory)
        fun saveInventory()
    }
}