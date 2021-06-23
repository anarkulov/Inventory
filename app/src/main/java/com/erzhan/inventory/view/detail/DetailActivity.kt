package com.erzhan.inventory.view.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.view.catalog.CatalogActivity.Companion.INVENTORY_KEY
import com.erzhan.inventory.model.data.toast
import com.erzhan.inventory.presenter.detail.DetailPresenter
import com.erzhan.inventory.view.editor.EditorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class DetailActivity : AppCompatActivity(), DetailContract.View {

    private lateinit var titleTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var quantityTextView: TextView
    private lateinit var currencyImageView: ImageView
    private lateinit var supplierTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imageImageView: ImageView
    private lateinit var presenter: DetailPresenter

    private var inventoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        title = "Details"

        titleTextView = findViewById(R.id.detailTitleId)
        priceTextView = findViewById(R.id.priceDetailTextId)
        locationTextView = findViewById(R.id.locationDetailTextViewId)
        quantityTextView = findViewById(R.id.quantityDetailTextViewId)
        currencyImageView = findViewById(R.id.currencyImageViewId)
        supplierTextView = findViewById(R.id.supplierDetailTextViewId)
        descriptionTextView = findViewById(R.id.descriptionDetailTextViewId)
        imageImageView = findViewById(R.id.imageDetailImageViewId)

        presenter = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(DetailPresenter::class.java)

        val bundle = intent.extras
        if (bundle != null) {
            inventoryId = bundle.getInt(INVENTORY_KEY)
            toast("Detail: $inventoryId")
            val inventory = presenter.getInventoryById(inventoryId)
            showSelectedInventory(inventory)
        } else {
            toast("Failed to load data")
        }
    }

//    private fun updateData() {
//        launch {
//            this@DetailActivity.let {
//                val inventory =
//                    InventoryDatabase(it).getInventoryDao().getInventoryById(inventoryId)
//                titleTextView.text = inventory.title
//                priceTextView.text = inventory.price.toString()
//                quantityTextView.text = inventory.quantity.toString()
//                locationTextView.text = inventory.location
//                supplierTextView.text = inventory.supplier
//                descriptionTextView.text = inventory.description
//
//                if (inventory.image != null) {
//                    imageImageView.setImageBitmap(inventory.image)
//                }
//
//                when (inventory.currency) {
//                    0 -> {
//                        currencyImageView.setImageResource(R.drawable.currency_som)
//                    }
//                    1 -> {
//                        currencyImageView.setImageResource(R.drawable.currency_dollar)
//                    }
//                    2 -> {
//                        currencyImageView.setImageResource(R.drawable.currency_ruble)
//                    }
//                    3 -> {
//                        currencyImageView.setImageResource(R.drawable.currency_tenge)
//                    }
//                }
//            }
//        }
//    }

//    override fun onResume() {
//        super.onResume()
////        updateData()
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        job.cancel()
//    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        return
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            editInventory(inventoryId)
            return true
        } else if (item.itemId == R.id.action_delete) {
            showDeleteConfirmationDialog()
            return true
        } else if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this@DetailActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(
            R.string.delete
        ) { _, _ ->
            presenter.removeInventory(inventoryId)
            finish()
        }
        builder.setNegativeButton(
            R.string.cancel
        ) { dialog, _ ->
            dialog?.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

//    private fun deleteInventory() {
//        if (inventoryId == -1) {
//            Toast.makeText(
//                this, getString(R.string.editor_delete_pet_failed),
//                Toast.LENGTH_SHORT
//            ).show()
//        } else {
//            launch {
//                this@DetailActivity.let {
//                    val dao = InventoryDatabase(it).getInventoryDao()
//                    val inventory = dao.getInventoryById(inventoryId!!)
//                    dao.deleteInventory(inventory)
//                    toast(getString(R.string.editor_delete_pet_successful))
//                }
//            }
//        }
//        finish()
//    }

//    private fun editInventory() {
//        val intent = Intent(this, EditorActivity::class.java)
//        intent.putExtra(
//            INVENTORY_KEY, inventoryId
//        )
//        startActivity(intent)
//    }

    override fun showSelectedInventory(inventory: Inventory) {
        titleTextView.text = inventory.title
        priceTextView.text = inventory.price.toString()
        quantityTextView.text = inventory.quantity.toString()
        locationTextView.text = inventory.location
        supplierTextView.text = inventory.supplier
        descriptionTextView.text = inventory.description

        if (inventory.image != null) {
            imageImageView.setImageBitmap(inventory.image)
        }

        when (inventory.currency) {
            0 -> {
                currencyImageView.setImageResource(R.drawable.currency_som)
            }
            1 -> {
                currencyImageView.setImageResource(R.drawable.currency_dollar)
            }
            2 -> {
                currencyImageView.setImageResource(R.drawable.currency_ruble)
            }
            3 -> {
                currencyImageView.setImageResource(R.drawable.currency_tenge)
            }
        }
    }

    override fun updateDataOnEdit(inventory: Inventory) {
        this.inventoryId = inventory.id
        showSelectedInventory(inventory)
    }

    override fun editInventory(inventoryId: Int) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(
            INVENTORY_KEY, inventoryId
        )
        startActivity(intent)
    }
}