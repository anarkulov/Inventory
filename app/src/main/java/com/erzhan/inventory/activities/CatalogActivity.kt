package com.erzhan.inventory.activities

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erzhan.inventory.R
import com.erzhan.inventory.adapter.InventoryRecyclerViewAdapter
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_CURRENCY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_DESCRIPTION
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_ID
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_LOCATION
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_TITLE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CONTENT_URI
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CURRENCY_DOLLAR
import com.erzhan.inventory.data.InventoryDbHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

class CatalogActivity : AppCompatActivity(), InventoryRecyclerViewAdapter.OnItemClickListener,
    LoaderManager.LoaderCallbacks<Cursor> {

    private val LOG = "Catalog Activity LOG"

    val INVENTORY_LOADER = 0
    private lateinit var dbHelper: InventoryDbHelper
    private lateinit var addFloatingActionButton: FloatingActionButton
    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var inventoryRecyclerViewAdapter: InventoryRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFloatingActionButton = findViewById(R.id.addFloatingActionButtonId);
        addFloatingActionButton.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        dbHelper = InventoryDbHelper(this)
        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerViewId)
        inventoryRecyclerView.layoutManager = GridLayoutManager(this, 2)
        inventoryRecyclerViewAdapter = InventoryRecyclerViewAdapter(this, null, this)

        inventoryRecyclerView.adapter = inventoryRecyclerViewAdapter

        supportLoaderManager.initLoader(INVENTORY_LOADER, null, this)
    }

    override fun onItemClick(inventoryId: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        val currentUri = ContentUris.withAppendedId(CONTENT_URI, inventoryId.toLong())

        intent.data = currentUri
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_insert_dummy_data) {
            insertDummyPet()
            return true
        } else if (item.itemId == R.id.action_delete_all_entries) {
            deletePets()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDummyPet() {

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image_placeholder)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteImage = stream.toByteArray()

        val values = ContentValues().apply {
            put(COLUMN_INVENTORY_TITLE, "Nothing")
            put(COLUMN_INVENTORY_PRICE, 300)
            put(COLUMN_INVENTORY_CURRENCY, CURRENCY_DOLLAR)
            put(COLUMN_INVENTORY_QUANTITY, 122)
            put(COLUMN_INVENTORY_SUPPLIER, "Sup inc")
            put(COLUMN_INVENTORY_LOCATION, "Bishkek")
            put(COLUMN_INVENTORY_DESCRIPTION, "Description is a description")
            put(COLUMN_INVENTORY_IMAGE, byteImage)
        }

        contentResolver.insert(CONTENT_URI, values)
    }

    private fun deletePets() {
        val rowId = contentResolver.delete(CONTENT_URI, null, null)
        Toast.makeText(this, "$rowId rows deleted from pet database", Toast.LENGTH_LONG).show()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
            COLUMN_INVENTORY_ID,
            COLUMN_INVENTORY_TITLE,
            COLUMN_INVENTORY_PRICE,
            COLUMN_INVENTORY_LOCATION,
            COLUMN_INVENTORY_CURRENCY,
            COLUMN_INVENTORY_QUANTITY,
            COLUMN_INVENTORY_SUPPLIER,
            COLUMN_INVENTORY_DESCRIPTION,
            COLUMN_INVENTORY_IMAGE
        )

        return CursorLoader(this, CONTENT_URI, projection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        inventoryRecyclerViewAdapter.changeCursor(data)
//        if (InventoryRecyclerViewAdapter.getItemCount() === 0) {
//            emptyView.setVisibility(View.VISIBLE)
//        } else {
//            emptyView.setVisibility(View.GONE)
//        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        inventoryRecyclerViewAdapter.changeCursor(null)
//        emptyView.setVisibility(View.VISIBLE)
    }
}