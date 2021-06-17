package com.erzhan.inventory.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.erzhan.inventory.data.InventoryContract.InventoryEntry

class InventoryDbHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "shelter.db"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${InventoryEntry.TABLE_NAME} (" +
                    "${InventoryEntry.COLUMN_INVENTORY_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${InventoryEntry.COLUMN_INVENTORY_TITLE} TEXT NOT NULL," +
                    "${InventoryEntry.COLUMN_INVENTORY_PRICE} INTEGER NOT NULL," +
                    "${InventoryEntry.COLUMN_INVENTORY_LOCATION} TEXT NOT NULL," +
                    "${InventoryEntry.COLUMN_INVENTORY_CURRENCY} INTEGER NOT NULL," +
                    "${InventoryEntry.COLUMN_INVENTORY_QUANTITY} INTEGER NOT NULL," +
                    "${InventoryEntry.COLUMN_INVENTORY_SUPPLIER} TEXT NOT NULL," +
                    "${InventoryEntry.COLUMN_INVENTORY_DESCRIPTION} TEXT NOT NULL," +
                    "${InventoryEntry.COLUMN_INVENTORY_IMAGE} BLOB NULL)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //
    }
}