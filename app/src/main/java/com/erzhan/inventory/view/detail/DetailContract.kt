package com.erzhan.inventory.view.detail

import com.erzhan.inventory.model.data.Inventory

interface DetailContract {

    interface Presenter {
        fun removeInventory(inventoryId: Int)
        fun getInventoryById(inventoryId: Int): Inventory
    }

    interface View {
        fun showSelectedInventory(inventory: Inventory)
        fun updateDataOnEdit(inventory: Inventory)
        fun editInventory(inventoryId: Int)
    }
}