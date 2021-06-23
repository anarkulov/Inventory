package com.erzhan.inventory.view.catalog

import com.erzhan.inventory.model.data.Inventory

interface CatalogContract {

    interface Presenter{
        fun deleteAllInventories()
        fun getAllInventories() : List<Inventory>
        fun insertDummyData()
    }

    interface View {
        fun showAllInventories(inventoryList: List<Inventory>)
        fun updateDataOnAdd(inventoryList: List<Inventory>)
    }
}