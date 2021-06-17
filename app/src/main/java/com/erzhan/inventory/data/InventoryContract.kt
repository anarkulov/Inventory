package com.erzhan.inventory.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.BaseColumns
import java.io.ByteArrayOutputStream

object InventoryContract {

    object InventoryEntry : BaseColumns {
        const val TABLE_NAME = "inventory"
        const val COLUMN_INVENTORY_ID = BaseColumns._ID
        const val COLUMN_INVENTORY_TITLE = "title"
        const val COLUMN_INVENTORY_PRICE = "price"
        const val COLUMN_INVENTORY_LOCATION = "location"
        const val COLUMN_INVENTORY_CURRENCY = "currency"
        const val COLUMN_INVENTORY_QUANTITY = "quantity"
        const val COLUMN_INVENTORY_SUPPLIER = "supplier"
        const val COLUMN_INVENTORY_DESCRIPTION = "description"
        const val COLUMN_INVENTORY_IMAGE = "image"

        const val CURRENCY_SOM = 0
        const val CURRENCY_DOLLAR = 1
        const val CURRENCY_RUBLE = 2
        const val CURRENCY_TENGE = 3

        const val CONTENT_AUTHORITY = "com.erzhan.inventory"
        private val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")
        const val PATH_INVENTORY = "inventory"
        val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY)

        fun isValidCurrency(currency: Int): Boolean {
            return currency == CURRENCY_SOM || currency == CURRENCY_DOLLAR || currency == CURRENCY_RUBLE || currency == CURRENCY_TENGE
        }

        // convert from bitmap to byte array
        fun getBytes(bitmap: Bitmap): ByteArray? {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
            val newImage = stream.toByteArray()
//            while (newImage.size > 50000) {
//                val newBitmap = BitmapFactory.decodeByteArray(newImage, 0, newImage.size);
//                val resized = Bitmap.createScaledBitmap(
//                    newBitmap,
//                    ((newBitmap.width * 0.8).toInt()),
//                    ((newBitmap.height * 0.8).toInt()),
//                    true
//                );
//                resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                newImage = stream.toByteArray();
//            }
            return newImage
        }

//         convert from byte array to bitmap
        fun getBitmap(image: ByteArray): Bitmap? {
            return BitmapFactory.decodeByteArray(image, 0, image.size)
        }

        const val CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY
        const val CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY
    }
}