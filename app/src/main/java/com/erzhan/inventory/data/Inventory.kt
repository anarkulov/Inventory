package com.erzhan.inventory.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Inventory(
    val title: String,
    val price: Int,
    val currency: Int,
    val quantity: Int,
    val location: String,
    val supplier: String,
    val description: String,
    val image: Bitmap?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    object Entry{
        const val DATABASE_NAME = "inventory"
        const val CURRENCY_SOM = 0
        const val CURRENCY_DOLLAR = 1
        const val CURRENCY_RUBLE = 2
        const val CURRENCY_TENGE = 3
    }
}