package com.erzhan.inventory.activities

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.erzhan.inventory.R
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_CURRENCY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_DESCRIPTION
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_ID
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_LOCATION
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_TITLE

class DetailActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit var titleTextView: TextView
    lateinit var priceTextView: TextView
    lateinit var locationTextView: TextView
    lateinit var quantityTextView: TextView
    lateinit var currencyImageView: ImageView
    lateinit var supplierTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var imageImageView: ImageView

    private val READ_EXTERNAL_STORAGE = 1
    private var currentUri: Uri? = null
    private val EXISTING_INVENTORY_LOADER = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        titleTextView = findViewById(R.id.detailTitleId)
        priceTextView = findViewById(R.id.priceEditTextId)
        locationTextView = findViewById(R.id.locationEditTextId)
        quantityTextView = findViewById(R.id.quantityEditTextId)
        currencyImageView = findViewById(R.id.spinnerEditId)
        supplierTextView = findViewById(R.id.supplierEditTextId)
        descriptionTextView = findViewById(R.id.descriptionEditTextId)
        imageImageView = findViewById(R.id.imageViewEditId)

        val intent = intent
        currentUri = intent.data

        if (currentUri != null) {
            title = "Details"
            supportLoaderManager.initLoader(EXISTING_INVENTORY_LOADER, null, this)
            Toast.makeText(this, "Detail: ${ContentUris.parseId(currentUri!!)}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
//        if (!petHasChanged) {
        super.onBackPressed()
        return
//        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
//        val discardButtonClickListener =
//            DialogInterface.OnClickListener { dialogInterface, i -> // User clicked "Discard" button, close the current activity.
//                finish()
//            }

        // Show dialog that there are unsaved changes
//        showUnsavedChangesDialog(discardButtonClickListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            editPet()
            return true
        } else if (item.itemId == R.id.action_delete) {
//            showDeleteConfirmationDialog()
            return true
        } else if (item.itemId == android.R.id.home) {
//            if (!petHasChanged) {
                NavUtils.navigateUpFromSameTask(this@DetailActivity)
                return true
//            }
//
//            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
//            // Create a click listener to handle the user confirming that
//            // changes should be discarded.
//            val discardButtonClickListener =
//                DialogInterface.OnClickListener { dialogInterface, i -> // User clicked "Discard" button, navigate to parent activity.
//                    NavUtils.navigateUpFromSameTask(this@EditorActivity)
//                }
//
//            // Show a dialog that notifies the user they have unsaved changes
//            showUnsavedChangesDialog(discardButtonClickListener)
//            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editPet() {
        val intent = Intent(this, EditorActivity::class.java)
        intent.data = currentUri
        startActivity(intent)
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

        return CursorLoader(
            this, currentUri!!, projection, null, null, null
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data == null || data.count < 1) {
            return
        }

        if (data.moveToFirst()) {
            val title = data.getString(data.getColumnIndex(COLUMN_INVENTORY_TITLE))
            val price = data.getString(data.getColumnIndex(COLUMN_INVENTORY_PRICE));
            val location = data.getString(data.getColumnIndex(COLUMN_INVENTORY_LOCATION));
            val quantity = data.getString(data.getColumnIndex(COLUMN_INVENTORY_QUANTITY));
            val currency = data.getInt(data.getColumnIndex(COLUMN_INVENTORY_CURRENCY));
            val supplier = data.getString(data.getColumnIndex(COLUMN_INVENTORY_SUPPLIER));
            val description = data.getString(data.getColumnIndex(COLUMN_INVENTORY_DESCRIPTION));
            val imageUri = data.getString(data.getColumnIndex(COLUMN_INVENTORY_IMAGE));

            titleTextView.text = title
            priceTextView.text = price
            locationTextView.text = location
            quantityTextView.text = quantity
            supplierTextView.text = supplier
            descriptionTextView.text = description
            if (imageUri != null){
                imageImageView.setImageURI(Uri.parse(imageUri))
            } else {
                imageImageView.setImageResource(R.drawable.image_placeholder)
            }

            when (currency) {
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
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        titleTextView.text = ""
        priceTextView.text = ""
        locationTextView.text = ""
        quantityTextView.text = ""
        descriptionTextView.text = ""
        imageImageView.setImageResource(R.drawable.image_placeholder)
        currencyImageView.setImageResource(R.drawable.currency_som)
    }
}