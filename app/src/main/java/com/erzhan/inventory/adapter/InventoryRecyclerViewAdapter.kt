package com.erzhan.inventory.adapter

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.database.getBlobOrNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erzhan.inventory.R
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_CURRENCY
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_ID
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_LOCATION
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_TITLE
import com.erzhan.inventory.data.InventoryContract.InventoryEntry.getBitmap


class InventoryRecyclerViewAdapter(context: Context,
                                   cursor: Cursor?,
                                   onItemCLickListener: OnItemClickListener
) :
    RecyclerView.Adapter<InventoryRecyclerViewAdapter.MyViewHolder>() {

    private var context: Context
    private var cursor: Cursor? = null
    private var dataValid: Boolean = false
    private val onItemClickListener: OnItemClickListener

    init {
        this.context = context
        this.onItemClickListener = onItemCLickListener
        init(cursor)
    }

    private fun init(cursor: Cursor?) {
        val cursorPresent = cursor != null
        this.cursor = cursor
        dataValid = cursorPresent
    }

    interface OnItemClickListener {
        fun onItemClick(inventoryId: Int)
    }

    class MyViewHolder(itemView: View, onItemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titleTextView: TextView
        var priceTextView: TextView
        var locationTextView: TextView
        var currencyImageView: ImageView
        var imageResource: ImageView
        var inventoryId: Int
        private var onItemClickListener: OnItemClickListener

        init {
            titleTextView = itemView.findViewById(R.id.inventoryTitleId)
            priceTextView = itemView.findViewById(R.id.inventoryPriceId)
            locationTextView = itemView.findViewById(R.id.inventoryLocationId)
            imageResource = itemView.findViewById(R.id.inventoryImageViewId)
            currencyImageView = itemView.findViewById(R.id.currencyIconId)
            inventoryId = -1
            this.onItemClickListener = onItemClickListener
            itemView.setOnClickListener { v: View ->
                onClick(
                    v
                )
            }
        }

        override fun onClick(v: View) {
            onItemClickListener.onItemClick(inventoryId)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_item, parent, false)

        return MyViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (!dataValid){
            throw IllegalStateException("this should only be called when the cursor is valid")
        }

        if (!cursor?.moveToPosition(position)!!){
            throw IllegalStateException("couldn't move cursor to position $position")
        }

        val title = cursor!!.getString(cursor!!.getColumnIndex(COLUMN_INVENTORY_TITLE))
        val price = cursor!!.getString(cursor!!.getColumnIndex(COLUMN_INVENTORY_PRICE));
        val location = cursor!!.getString(cursor!!.getColumnIndex(COLUMN_INVENTORY_LOCATION));
        val currency = cursor!!.getInt(cursor!!.getColumnIndex(COLUMN_INVENTORY_CURRENCY));
        val id = cursor?.getInt(cursor!!.getColumnIndex(COLUMN_INVENTORY_ID));

        if (cursor!!.getBlob(cursor!!.getColumnIndex(COLUMN_INVENTORY_IMAGE)) != null) {
            val image = cursor!!.getBlob(cursor!!.getColumnIndex(COLUMN_INVENTORY_IMAGE));
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            holder.imageResource.setImageBitmap(bitmap)
        }


        val image = cursor!!.getBlob(cursor!!.getColumnIndex(COLUMN_INVENTORY_IMAGE))

        if (image != null){
            val bitmap = getBitmap(image)
            holder.imageResource.setImageBitmap(bitmap)
        } else {
            holder.imageResource.setImageResource(R.drawable.image_placeholder)
        }

        if (id != null) {
            holder.inventoryId = id
        }
        holder.titleTextView.text = title
        holder.priceTextView.text = price
        holder.locationTextView.text = location

        when (currency) {
            0 -> {
                holder.currencyImageView.setImageResource(R.drawable.currency_som)
            }
            1 -> {
                holder.currencyImageView.setImageResource(R.drawable.currency_dollar)
            }
            2 -> {
                holder.currencyImageView.setImageResource(R.drawable.currency_ruble)
            }
            3 -> {
                holder.currencyImageView.setImageResource(R.drawable.currency_tenge)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataValid && cursor != null) {
            cursor!!.count
        } else 0
    }

    fun changeCursor(data: Cursor?) {
        swapCursor(data)?.close()
    }

    private fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == cursor) {
            return null
        }

        val oldCursor = cursor
        cursor = newCursor

        if (newCursor != null) {
            dataValid = true
            notifyDataSetChanged()
        } else {
            dataValid = false
        }
        return oldCursor
    }
}
