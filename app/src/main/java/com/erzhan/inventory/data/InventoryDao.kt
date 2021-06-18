package com.erzhan.inventory.data

import androidx.room.*

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY id DESC")
    suspend fun getAllInventory(): List<Inventory>

    @Query("SELECT * FROM inventory WHERE id = :inventoryId")
    suspend fun getInventoryById(inventoryId: Int) : Inventory

    @Insert
    suspend fun addInventory(inventory: Inventory)

    @Update
    suspend fun updateInventory(inventory: Inventory)

    @Delete
    suspend fun deleteInventory(inventory: Inventory)

    @Delete
    suspend fun deleteAll(noteList: List<Inventory>)
}