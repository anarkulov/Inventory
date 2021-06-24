package com.erzhan.inventory.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.model.data.InventoryDatabase
import com.erzhan.inventory.presenter.CatalogPresenter
import com.erzhan.inventory.view.adapter.InventoryRecyclerViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class CatalogActivity : AppCompatActivity(), InventoryRecyclerViewAdapter.OnItemClickListener,
    MyContract.CatalogView {

    companion object {
        const val INVENTORY_KEY = "INVENTORY_ID"
    }

    private lateinit var inventoryRecyclerViewAdapter: InventoryRecyclerViewAdapter
    private lateinit var addFloatingActionButton: FloatingActionButton
    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var inventoryList: ArrayList<Inventory>
    private lateinit var emptyList: LinearLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var presenter: CatalogPresenter
    private lateinit var repository: InventoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = InventoryRepository(InventoryDatabase(this).getInventoryDao())
        presenter = CatalogPresenter(repository, this)

        addFloatingActionButton = findViewById(R.id.addFloatingActionButtonId);
        addFloatingActionButton.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        emptyList = findViewById(R.id.emptyLayoutId)
        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerViewId)
        inventoryRecyclerView.layoutManager = GridLayoutManager(this, 2)

        inventoryList = ArrayList()

        loadingProgressBar = findViewById(R.id.loading_progressbar_id)
    }

    override fun showAllInventories(inventoryList: List<Inventory>) {
        inventoryRecyclerViewAdapter = InventoryRecyclerViewAdapter(this, inventoryList, this)
        inventoryRecyclerView.adapter = inventoryRecyclerViewAdapter
        setVisibility()
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun updateDataOnAdd(inventoryList: List<Inventory>) {
        showAllInventories(inventoryList)
    }

    override fun onItemClick(inventoryId: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(INVENTORY_KEY, inventoryId)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_insert_dummy_data) {
            presenter.insertDummyData()
            return true
        } else if (item.itemId == R.id.action_delete_all_entries) {
            presenter.deleteAllInventories()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setVisibility() {
        loadingProgressBar.visibility = View.GONE
        if (inventoryRecyclerViewAdapter.itemCount == 0){
            emptyList.visibility = View.VISIBLE
            inventoryRecyclerView.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            inventoryRecyclerView.visibility = View.VISIBLE
        }

        inventoryRecyclerViewAdapter.notifyDataSetChanged()
    }
}