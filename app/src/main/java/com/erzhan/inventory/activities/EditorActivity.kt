package com.erzhan.inventory.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
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
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CONTENT_URI
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CURRENCY_DOLLAR
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CURRENCY_RUBLE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CURRENCY_SOM
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.CURRENCY_TENGE
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class EditorActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit var currencySpinner: Spinner
    var currency = 0

    lateinit var titleEditText: EditText
    lateinit var priceEditText: EditText
    lateinit var quantityTextView: TextView
    lateinit var supplierEditText: EditText
    lateinit var descriptionEditText: EditText
    private lateinit var locationEditText: EditText
    lateinit var incrementButton: Button
    lateinit var decrementButton: Button

    lateinit var imageImageView: ImageView
    lateinit var chooseImageButton: Button
    lateinit var filePath: Uri
//    lateinit var imageProjection : Array<String>

    var currentUri: Uri? = null
    private val EXISTING_INVENTORY_LOADER_EDIT = 2;
    var inventoryHasChanged = false
    val READ_EXTERNAL_STORAGE = 1
    val GET_IMAGE = 1
    var number = 0
    val QUANTITY_TEXT_KEY = "KEY"
    val URI_TEXT_KEY = "Uri key"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

        val intent = intent
        currentUri = intent.data

        if (currentUri != null) {
            title = "Edit"
            supportLoaderManager.initLoader(EXISTING_INVENTORY_LOADER_EDIT, null, this)
            Toast.makeText(this, "Editor: ${ContentUris.parseId(currentUri!!)}", Toast.LENGTH_LONG)
                .show()
        }

        setupSpinner()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            QUANTITY_TEXT_KEY,
            quantityTextView.text.toString()
        )

        outState.putString(
            URI_TEXT_KEY,
            currentUri.toString()
        )

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        quantityTextView.text = savedInstanceState.getString(QUANTITY_TEXT_KEY)
        currentUri = Uri.parse(savedInstanceState.getString(URI_TEXT_KEY))
    }

    private fun chooseFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
//        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        Intent.createChooser(intent, "Choose an Image")
        startActivityForResult(intent, GET_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GET_IMAGE && resultCode == Activity.RESULT_OK){
            if (data != null) {
                filePath = data.data!!
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                } catch (e: FileNotFoundException){
                    e.printStackTrace();
                } catch (e: IOException) {
                    e.printStackTrace();
                }

                if (bitmap != null){
                    imageImageView.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE)
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
            READ_EXTERNAL_STORAGE -> {

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
                } else if (selection == getString(R.string.currency_tenge)){
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
            DialogInterface.OnClickListener { dialogInterface, i ->
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

    private fun saveInventory() {
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

        // store image into database
//        try {
////            val fis = contentResolver.openInputStream(filePath)
////            val buf = BufferedInputStream(fis)
////            val bitmap = BitmapFactory.decodeStream(buf)
////            Log.v("LOG EDITOR", fis.toString())
////            image = getBytes(bitmap)
//
//        } catch (e: FileNotFoundException){
//            Log.v("LOG EDITOR", e.toString())
//        }


        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(price) || TextUtils.isEmpty(location) || TextUtils.isEmpty(
                quantity
            )
            || TextUtils.isEmpty(supplier) || TextUtils.isEmpty(description)
        ) {
            Toast.makeText(this, "Please fill out all the field", Toast.LENGTH_LONG).show()
            return
        }

//        if (currentUri == null &&
//            TextUtils.isEmpty(title) && TextUtils.isEmpty(price) && TextUtils.isEmpty(location) && TextUtils.isEmpty(
//                quantity
//            )
//            && TextUtils.isEmpty(supplier) && TextUtils.isEmpty(description) && currency == CURRENCY_SOM
//        ) {
//            return
//        }

        val intPrice = Integer.parseInt(price)
        val intQuantity = Integer.parseInt(price)

        if (intPrice < 0 || intQuantity < 0) {
            Toast.makeText(this, "Price or Quantity is not valid", Toast.LENGTH_LONG).show()
        }

        val image: BitmapDrawable = imageImageView.drawable as BitmapDrawable
        val bitmap = image.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        val byteImage = stream.toByteArray()

        val values: ContentValues = ContentValues().apply {
            put(COLUMN_INVENTORY_TITLE, title)
            put(COLUMN_INVENTORY_PRICE, intPrice)
            put(COLUMN_INVENTORY_CURRENCY, currency)
            put(COLUMN_INVENTORY_QUANTITY, intQuantity)
            put(COLUMN_INVENTORY_SUPPLIER, supplier)
            put(COLUMN_INVENTORY_LOCATION, location)
            put(COLUMN_INVENTORY_DESCRIPTION, description)
            put(COLUMN_INVENTORY_IMAGE, byteImage)
        }

        if (currentUri == null) {
            val newUri = contentResolver.insert(CONTENT_URI, values)

            if (newUri == null) {
                Toast.makeText(
                    this, getString(R.string.editor_insert_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, getString(R.string.editor_insert_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val rowsAffected = contentResolver.update(currentUri!!, values, null, null)

            if (rowsAffected == 0) {
                Toast.makeText(
                    this, getString(R.string.editor_update_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, getString(R.string.editor_update_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        finish()
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
            val supplier = data.getString(data.getColumnIndex(COLUMN_INVENTORY_SUPPLIER));
            val description = data.getString(data.getColumnIndex(COLUMN_INVENTORY_DESCRIPTION));
            val image = data.getBlob(data.getColumnIndex(COLUMN_INVENTORY_IMAGE))

            if (image != null) {
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                imageImageView.setImageBitmap(bitmap)
            } else {
                imageImageView.setImageResource(R.drawable.image_placeholder)
            }

            titleEditText.setText(title)
            priceEditText.setText(price)
            locationEditText.setText(location)
            quantityTextView.text = quantity
            supplierEditText.setText(supplier)
            descriptionEditText.setText(description)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        titleEditText.setText("")
        priceEditText.setText("")
        locationEditText.setText("")
        quantityTextView.text = ""
        supplierEditText.setText("")
        descriptionEditText.setText("")
        imageImageView.setImageResource(R.drawable.image_placeholder)
    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener =
        OnTouchListener { _, _ ->
            inventoryHasChanged = true
            false
        }
}