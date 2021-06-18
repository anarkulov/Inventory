package com.erzhan.inventory.activities

import android.content.Intent

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erzhan.inventory.R
import com.erzhan.inventory.adapter.InventoryRecyclerViewAdapter
import com.erzhan.inventory.data.Inventory
import com.erzhan.inventory.data.Inventory.Entry.CURRENCY_DOLLAR
import com.erzhan.inventory.data.InventoryDatabase
import com.erzhan.inventory.data.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CatalogActivity : AppCompatActivity(), InventoryRecyclerViewAdapter.OnItemClickListener, CoroutineScope{

    companion object {
        const val INVENTORY_KEY = "INVENTORY_ID"
    }

    private lateinit var inventoryRecyclerViewAdapter: InventoryRecyclerViewAdapter
    private lateinit var addFloatingActionButton: FloatingActionButton
    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var inventoryList: ArrayList<Inventory>

    private lateinit var job: Job

    // CoroutineContext
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()
        addFloatingActionButton = findViewById(R.id.addFloatingActionButtonId);
        addFloatingActionButton.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

// RecyclerView
        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerViewId)
        inventoryRecyclerView.layoutManager = GridLayoutManager(this, 2)
        inventoryList = ArrayList()

        launch {
            this@CatalogActivity.let {
                inventoryList = InventoryDatabase(it).getInventoryDao().getAllInventory() as ArrayList<Inventory>
                inventoryRecyclerViewAdapter = InventoryRecyclerViewAdapter(it, inventoryList, it)
                inventoryRecyclerView.adapter = inventoryRecyclerViewAdapter
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

// Open Item Details on click
    override fun onItemClick(inventoryId: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(INVENTORY_KEY, inventoryId)
        startActivity(intent)
    }

// Set menu items
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_insert_dummy_data) {
            insertDummyData()
            return true
        } else if (item.itemId == R.id.action_delete_all_entries) {
            deleteAllData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDummyData() {
        launch {
            this@CatalogActivity.let {
                val bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.image_placeholder)
                val newInventoryDummyData = Inventory(
                    "Dummy Title", 100, CURRENCY_DOLLAR,
                    2, "Bishkek", "D inc",
                    "This is the sample data for this item", bitmapImage
                )
                InventoryDatabase(it).getInventoryDao().addInventory(newInventoryDummyData)
                toast("Successfully inserted")
            }
        }
    }

    private fun deleteAllData() {
        launch {
            this@CatalogActivity.let {
                InventoryDatabase(it).getInventoryDao().deleteAll(inventoryList)
                toast("Successfully deleted")
            }
        }
    }
}