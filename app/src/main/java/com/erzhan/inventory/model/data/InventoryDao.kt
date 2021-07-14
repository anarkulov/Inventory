package com.erzhan.inventory.model.data

import androidx.room.*

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY id DESC limit 200")
    suspend fun getAllInventory(): List<Inventory>

    @Query("SELECT * FROM inventory WHERE id = :inventoryId")
    suspend fun getInventoryById(inventoryId: Int) : Inventory

    @Insert
    suspend fun addInventory(inventory: Inventory)

    @Update
    suspend fun updateInventory(inventory: Inventory)

    @Delete
    suspend fun deleteInventory(inventory: Inventory)

    @Query("DELETE FROM inventory")
    suspend fun deleteAll()
}