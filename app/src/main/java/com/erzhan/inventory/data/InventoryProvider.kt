package com.erzhan.inventory.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_CURRENCY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_DESCRIPTION
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_ID
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_LOCATION
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_TITLE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CONTENT_AUTHORITY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CONTENT_LIST_TYPE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.PATH_INVENTORY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.TABLE_NAME
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.isValidCurrency

class InventoryProvider : ContentProvider() {

    companion object {
        val LOG_TAG: String = InventoryProvider::class.java.simpleName

        private const val INVENTORY = 1000
        private const val INVENTORY_ID = 1001

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY, INVENTORY);
            uriMatcher.addURI(CONTENT_AUTHORITY, "$PATH_INVENTORY/#", INVENTORY_ID);
        }
    }

    private lateinit var dbHelper: InventoryDbHelper

    override fun onCreate(): Boolean {
        dbHelper = InventoryDbHelper(context!!);
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val database: SQLiteDatabase = dbHelper.readableDatabase

        lateinit var cursor: Cursor

        val match: Int = uriMatcher.match(uri)

        if (match == INVENTORY) {
            cursor = database.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
        } else if (match == INVENTORY_ID) {
            val newSelection = "$COLUMN_INVENTORY_ID=?"
            val newSelectionArgs = arrayOf(ContentUris.parseId(uri).toString())
            cursor = database.query(
                TABLE_NAME,
                projection,
                newSelection,
                newSelectionArgs,
                null,
                null,
                sortOrder
            )
        } else {
            throw IllegalArgumentException("Cannot query unknown URI $uri")
        }

        cursor.setNotificationUri(context!!.contentResolver, uri)

        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        //
        val match = uriMatcher.match(uri)

        return if (match == INVENTORY) {
            insertInventory(uri, values)
        } else {
            throw java.lang.IllegalArgumentException("Insert is not supported for $uri")
        }
    }

    private fun insertInventory(uri: Uri, contentValues: ContentValues?): Uri? {
        if (contentValues == null) {
            return null
        }
        val title = contentValues.getAsString(COLUMN_INVENTORY_TITLE)
        val price = contentValues.getAsInteger(COLUMN_INVENTORY_PRICE)
        val location = contentValues.getAsString(COLUMN_INVENTORY_LOCATION)
        val currency = contentValues.getAsInteger(COLUMN_INVENTORY_CURRENCY)
        val quantity = contentValues.getAsInteger(COLUMN_INVENTORY_QUANTITY)
        val supplier = contentValues.getAsString(COLUMN_INVENTORY_SUPPLIER)
        val description = contentValues.getAsString(COLUMN_INVENTORY_DESCRIPTION)
//        val image = contentValues.get(COLUMN_INVENTORY_IMAGE)

        if (title == null) {
            throw IllegalArgumentException("Invalid data storing: Inventory requires valid title");
        }

        if (price == null || price < 1) {
            throw IllegalArgumentException("Invalid data storing: Inventory requires valid price");
        }

        if (location == null) {
            throw IllegalArgumentException("Invalid data storing: Inventory requires valid location");
        }

        if (currency == null || !isValidCurrency(currency)) {
            throw IllegalArgumentException("Invalid data storing: Inventory requires valid currency");
        }

        if (quantity == null || quantity < 0) {
            throw IllegalArgumentException("Invalid data storing: Inventory requires valid quantity");
        }

        if (supplier == null) {
            throw IllegalArgumentException("Invalid data storing: Inventory requires valid supplier");
        }

        if (description == null) {
            throw IllegalArgumentException("Invalid data storing: Inventory requires valid description");
        }

        val database = dbHelper.writableDatabase

        val newRow = database.insert(TABLE_NAME, null, contentValues)

        if (newRow == (-1).toLong()) {
            Log.e(LOG_TAG, "Failed to insert row for $uri")
            return null
        }

        context!!.contentResolver.notifyChange(uri, null)

        return ContentUris.withAppendedId(uri, newRow)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val matcher = uriMatcher.match(uri)

        return if (matcher == INVENTORY) {
            deleteInventory(uri, selection, selectionArgs)
        } else if (matcher == INVENTORY_ID) {
            val newSelection = "$COLUMN_INVENTORY_ID=?"
            val newSelectionArgs = arrayOf(ContentUris.parseId(uri).toString())
            deleteInventory(uri, newSelection, newSelectionArgs)
        } else {
            throw IllegalArgumentException("Delete is not supported for $uri");
        }
    }

    private fun deleteInventory(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val database = dbHelper.writableDatabase

        val deletedRow = database.delete(TABLE_NAME, selection, selectionArgs)
        context!!.contentResolver.notifyChange(uri, null)

        return deletedRow
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {

        val matcher = uriMatcher.match(uri)

        return if (matcher == INVENTORY) {
            updateInventory(uri, values, selection, selectionArgs)
        } else if (matcher == INVENTORY_ID) {
            val newSelection = "$COLUMN_INVENTORY_ID=?"
            val newSelectionArgs = arrayOf(ContentUris.parseId(uri).toString())
            updateInventory(uri, values, newSelection, newSelectionArgs)
        } else {
            throw IllegalArgumentException("Update is not supported for $uri");
        }
    }

    private fun updateInventory(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        if (values == null) {
            return 0
        }

        if (values.containsKey(COLUMN_INVENTORY_TITLE)) {
            values.getAsString(COLUMN_INVENTORY_TITLE)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a title")
        }

        if (values.containsKey(COLUMN_INVENTORY_PRICE)) {
            values.getAsString(COLUMN_INVENTORY_PRICE)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a price")
        }

        if (values.containsKey(COLUMN_INVENTORY_LOCATION)) {
            values.getAsString(COLUMN_INVENTORY_LOCATION)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a location")
        }

        if (values.containsKey(COLUMN_INVENTORY_CURRENCY)) {
            values.getAsString(COLUMN_INVENTORY_CURRENCY)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a currency")
        }

        if (values.containsKey(COLUMN_INVENTORY_QUANTITY)) {
            values.getAsString(COLUMN_INVENTORY_QUANTITY)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a quantity")
        }

        if (values.containsKey(COLUMN_INVENTORY_SUPPLIER)) {
            values.getAsString(COLUMN_INVENTORY_SUPPLIER)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a supplier")
        }

        if (values.containsKey(COLUMN_INVENTORY_DESCRIPTION)) {
            values.getAsString(COLUMN_INVENTORY_DESCRIPTION)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a description")
        }

        if (values.containsKey(COLUMN_INVENTORY_IMAGE)) {
            values.getAsString(COLUMN_INVENTORY_IMAGE)
                ?: throw java.lang.IllegalArgumentException("Inventory requires a image")
        }

        if (values.size() == 0){
            return 0
        }

        val database = dbHelper.writableDatabase

        context!!.contentResolver.notifyChange(uri, null)

        return database.update(TABLE_NAME, values, selection, selectionArgs)
    }

    override fun getType(uri: Uri): String? {

        val match: Int = uriMatcher.match(uri)
        return when (match) {
            INVENTORY -> CONTENT_LIST_TYPE
            INVENTORY_ID -> CONTENT_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }
}