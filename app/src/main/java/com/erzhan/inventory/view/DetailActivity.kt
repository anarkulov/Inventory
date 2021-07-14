package com.erzhan.inventory.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.erzhan.inventory.InventoryRepository
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.model.data.InventoryDatabase
import com.erzhan.inventory.view.CatalogActivity.Companion.INVENTORY_KEY
import com.erzhan.inventory.model.data.toast
import com.erzhan.inventory.presenter.DetailPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(), MyContract.DetailView {

    private lateinit var titleTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var quantityTextView: TextView
    private lateinit var currencyImageView: ImageView
    private lateinit var supplierTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imageImageView: ImageView
    private lateinit var presenter: DetailPresenter
    private lateinit var repository: InventoryRepository

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

        repository = InventoryRepository(InventoryDatabase(this).getInventoryDao())
        presenter = DetailPresenter(repository, this)

        val bundle = intent.extras
        if (bundle != null) {
            inventoryId = bundle.getInt(INVENTORY_KEY)
            toast("Detail: $inventoryId")
            CoroutineScope(Dispatchers.Main).launch {
                val inventory = presenter.getInventoryById(inventoryId)
                showSelectedInventory(inventory)
            }
        } else {
            toast("Failed to load data")
        }

        locationTextView.setOnClickListener {
            onClickLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            val inventory = presenter.getInventoryById(inventoryId)
            updateDataOnEdit(inventory)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            onClickEditInventoryButton(inventoryId)
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
//        this.inventoryId = inventory.id
        showSelectedInventory(inventory)
    }

    override fun onClickEditInventoryButton(inventoryId: Int) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(
            INVENTORY_KEY, inventoryId
        )
        startActivity(intent)
    }

    override fun onClickLocation() {
        val mapLocal = locationTextView.text.toString()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$mapLocal"))
        val chooser = Intent.createChooser(intent, "Choose the app")
        try {
            startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Could find the app to open location",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}