package com.erzhan.inventory.view.editor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.erzhan.inventory.R
import com.erzhan.inventory.view.catalog.CatalogActivity.Companion.INVENTORY_KEY
import com.erzhan.inventory.model.data.Inventory
import com.erzhan.inventory.model.data.Inventory.Entry.CURRENCY_DOLLAR
import com.erzhan.inventory.model.data.Inventory.Entry.CURRENCY_RUBLE
import com.erzhan.inventory.model.data.Inventory.Entry.CURRENCY_SOM
import com.erzhan.inventory.model.data.Inventory.Entry.CURRENCY_TENGE
import com.erzhan.inventory.model.data.InventoryDatabase
import com.erzhan.inventory.model.data.toast
import com.erzhan.inventory.presenter.detail.DetailPresenter
import com.erzhan.inventory.presenter.editor.EditorPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.coroutines.CoroutineContext


class EditorActivity : AppCompatActivity(), EditorContract.View {

    companion object {
        private const val IMAGE_SAVE_KEY = "IMAGE SAVE KEY"
        private const val QUANTITY_SAVE_KEY = "QUANTITY TEXT KEY"
        private const val INVENTORY_ID_SAVE_KEY = "INVENTORY ID KEY"
        private const val GET_IMAGE_REQUEST_CODE = 1000
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1001
    }

    private var inventoryId = -1
    private var currency = 0
    private var number = 0
    private var inventoryHasChanged = false

    private lateinit var titleEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var quantityTextView: TextView
    private lateinit var locationEditText: EditText
    private lateinit var supplierEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var imageImageView: ImageView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var chooseImageButton: Button
    private lateinit var currencySpinner: Spinner
    private lateinit var presenter: EditorPresenter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        presenter = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(EditorPresenter::class.java)

        currencySpinner = findViewById(R.id.spinnerEditId)

        titleEditText = findViewById(R.id.titleEditTextId)
        priceEditText = findViewById(R.id.priceEditTextId)
        quantityTextView = findViewById(R.id.quantityText)
        supplierEditText = findViewById(R.id.supplierEditTextId)
        descriptionEditText = findViewById(R.id.descriptionEditTextId)
        locationEditText = findViewById(R.id.locationEditTextId)
        imageImageView = findViewById(R.id.imageViewEditId)
        chooseImageButton = findViewById(R.id.chooseImageButtonId)
        chooseImageButton.setOnClickListener {
            getPermission()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                chooseFile()
            }
        }
        incrementButton = findViewById(R.id.quantity_add)
        incrementButton.setOnClickListener {
            number = quantityTextView.text.toString().toInt()
            number++
            quantityTextView.text = number.toString()
        }
        decrementButton = findViewById(R.id.quantity_sub)
        decrementButton.setOnClickListener {
            number = quantityTextView.text.toString().toInt()
            if (number > 0) {
                number--
            }
            quantityTextView.text = number.toString()
        }

        titleEditText.setOnTouchListener(touchListener)
        priceEditText.setOnTouchListener(touchListener)
        quantityTextView.setOnTouchListener(touchListener)
        supplierEditText.setOnTouchListener(touchListener)
        descriptionEditText.setOnTouchListener(touchListener)
        locationEditText.setOnTouchListener(touchListener)
        chooseImageButton.setOnTouchListener(touchListener)

        val bundle = intent.extras
        if (bundle != null) {
            inventoryId = bundle.getInt(INVENTORY_KEY)
            toast("Detail: $inventoryId")

            if (inventoryId != -1) {
                val inventory = presenter.getInventoryById(inventoryId)
                showSelectedInventory(inventory)
            }
        } else {
            toast("Failed to load data: Invalid id")
        }
        setupSpinner()
    }


    private fun drawableToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        if (imageImageView.drawable != null) {
            val bitmap = imageImageView.drawable.toBitmap()
            val imageByteArray: ByteArray = drawableToByteArray(bitmap)
            outState.putByteArray(IMAGE_SAVE_KEY, imageByteArray)
        }
        super.onSaveInstanceState(outState)
        outState.putString(
            QUANTITY_SAVE_KEY,
            quantityTextView.text.toString()
        )
        outState.putString(
            INVENTORY_ID_SAVE_KEY,
            inventoryId.toString()
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        quantityTextView.text = savedInstanceState.getString(QUANTITY_SAVE_KEY)

        savedInstanceState.getString(INVENTORY_ID_SAVE_KEY)
            .also { inventoryId = Integer.parseInt(it!!) }
        val byteArray = savedInstanceState.getByteArray(IMAGE_SAVE_KEY)
        val bitmapImage = byteArray?.let { BitmapFactory.decodeByteArray(byteArray, 0, it.size) }
        imageImageView.setImageBitmap(bitmapImage)
    }

    private fun chooseFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        Intent.createChooser(intent, "Choose an Image")
        startActivityForResult(intent, GET_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GET_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val filePath = data.data!!
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    imageImageView.setImageBitmap(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace();
                } catch (e: IOException) {
                    e.printStackTrace();
                }
            }
        }
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_PERMISSION_CODE -> {

                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupSpinner() {
        val currencySpinnerAdapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this,
            R.array.array_currency_options, android.R.layout.simple_spinner_item
        )

        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        currencySpinner.adapter = currencySpinnerAdapter

        currencySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selection = position.let { parent?.getItemAtPosition(it) } as String
                currency = if (selection == getString(R.string.currency_som)) {
                    CURRENCY_SOM
                } else if (selection == getString(R.string.currency_dollar)) {
                    CURRENCY_DOLLAR
                } else if (selection == getString(R.string.currency_ruble)) {
                    CURRENCY_RUBLE
                } else if (selection == getString(R.string.currency_tenge)) {
                    CURRENCY_TENGE
                } else {
                    currency
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                currency = 0 // som
            }
        }
    }

    override fun onBackPressed() {
        if (!inventoryHasChanged) {
            super.onBackPressed()
            return
        }

        val discardButtonClickListener =
            DialogInterface.OnClickListener { _, _ ->
                finish()
            }

        showUnsavedChangesDialog(discardButtonClickListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_saved) {
            saveInventory()
            return true
        } else if (item.itemId == android.R.id.home) {
            if (!inventoryHasChanged) {
                NavUtils.navigateUpFromSameTask(this@EditorActivity)
                return true
            }
            val discardButtonClickListener =
                DialogInterface.OnClickListener { _, _ ->
                    NavUtils.navigateUpFromSameTask(this@EditorActivity)
                }

            showUnsavedChangesDialog(discardButtonClickListener)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showUnsavedChangesDialog(discardButtonClickListener: DialogInterface.OnClickListener) {

        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setPositiveButton(R.string.discard, discardButtonClickListener)
        builder.setNegativeButton(
            R.string.keep_editing
        ) { dialog, _ ->
            dialog?.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun showSelectedInventory(inventory: Inventory) {
        titleEditText.setText(inventory.title)
        priceEditText.setText(inventory.price.toString())
        quantityTextView.text = inventory.quantity.toString()
        supplierEditText.setText(inventory.supplier)
        descriptionEditText.setText(inventory.description)
        locationEditText.setText(inventory.location)

        if (inventory.image != null) {
            imageImageView.setImageBitmap(inventory.image)
        }
        toast("Successfully loaded data")
    }

    override fun updateDataOnEdit(inventory: Inventory) {
        this.inventoryId = inventory.id
        showSelectedInventory(inventory)
    }

    override fun saveInventory() {
        if (!inventoryHasChanged) {
            finish()
            return
        }

        val title = titleEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()
        val location = locationEditText.text.toString().trim()
        val quantity = quantityTextView.text.toString().trim()
        val supplier = supplierEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(price) || TextUtils.isEmpty(location) || TextUtils.isEmpty(
                quantity
            )
            || TextUtils.isEmpty(supplier) || TextUtils.isEmpty(description)
        ) {
            Toast.makeText(this, "Please fill out all the field", Toast.LENGTH_LONG).show()
            return
        }

        val intPrice = Integer.parseInt(price)
        val intQuantity = Integer.parseInt(price)

        if (intPrice < 0 || intQuantity < 0) {
            Toast.makeText(this, "Price or Quantity is not valid", Toast.LENGTH_LONG).show()
        }

        val image: BitmapDrawable = imageImageView.drawable as BitmapDrawable
        val bitmapImage = image.bitmap

        if (inventoryId == -1) {
            val inventory = Inventory(
                title, intPrice, currency, intQuantity,
                location, supplier, description, bitmapImage
            )
            presenter.addInventory(inventory)
            toast("Successfully inserted")
        } else {
            val inventory = Inventory(
                title, intPrice, currency, intQuantity,
                location, supplier, description, bitmapImage
            )

            inventory.id = inventoryId
            presenter.updateInventory(inventory)
            toast("Successfully updated")
        }
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener =
        OnTouchListener { _, _ ->
            inventoryHasChanged = true
            false
        }
}