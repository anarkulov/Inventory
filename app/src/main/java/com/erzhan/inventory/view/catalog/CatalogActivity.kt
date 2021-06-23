package com.erzhan.inventory.view.catalog

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.presenter.catalog.CatalogPresenter
import com.erzhan.inventory.view.adapter.InventoryRecyclerViewAdapter
import com.erzhan.inventory.view.detail.DetailActivity
import com.erzhan.inventory.view.editor.EditorActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class CatalogActivity : AppCompatActivity(), InventoryRecyclerViewAdapter.OnItemClickListener, CatalogContract.View{

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application))
            .get(CatalogPresenter::class.java)

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

//        showAllInventories(presenter.getAllInventories())
    }

    override fun showAllInventories(inventoryList: List<Inventory>) {
        inventoryRecyclerViewAdapter = InventoryRecyclerViewAdapter(this, inventoryList, this)
        inventoryRecyclerView.adapter = inventoryRecyclerViewAdapter
    }

    override fun updateDataOnAdd(inventoryList: List<Inventory>) {
        this.inventoryList = inventoryList as ArrayList<Inventory>
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

//    private fun insertDummyData() {
//        launch {
//            this@CatalogActivity.let {
//                val bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.image_placeholder)
//                val newInventoryDummyData = Inventory(
//                    "Dummy Title", 100, CURRENCY_DOLLAR,
//                    2, "Bishkek", "D inc",
//                    "This is the sample data for this item", bitmapImage
//                )
//                InventoryDatabase(it).getInventoryDao().addInventory(newInventoryDummyData)
//                toast("Successfully inserted")
//                updateData()
//            }
//        }
//    }

//    private fun deleteAllData() {
//        launch {
//            this@CatalogActivity.let {
//                InventoryDatabase(it).getInventoryDao().deleteAll()
//                toast("Successfully deleted")
//                updateData()
//            }
//        }
//    }

//    private fun setVisibility() {
//        loadingProgressBar.visibility = View.GONE
//        if (inventoryRecyclerViewAdapter.itemCount == 0){
//            emptyList.visibility = View.VISIBLE
//            inventoryRecyclerView.visibility = View.GONE
//        } else {
//            emptyList.visibility = View.GONE
//            inventoryRecyclerView.visibility = View.VISIBLE
//        }
//
//        inventoryRecyclerViewAdapter.notifyDataSetChanged()
//    }

}