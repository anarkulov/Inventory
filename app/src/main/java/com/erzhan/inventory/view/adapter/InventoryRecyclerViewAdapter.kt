package com.erzhan.inventory.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erzhan.inventory.R
import com.erzhan.inventory.model.data.Inventory


class InventoryRecyclerViewAdapter(
    context: Context,
    inventoryList: List<Inventory>,
    onItemCLickListener: OnItemClickListener
) :
    RecyclerView.Adapter<InventoryRecyclerViewAdapter.MyViewHolder>() {

    private var context: Context
    private var inventoryList: List<Inventory>
    private val onItemClickListener: OnItemClickListener

    init {
        this.context = context
        this.inventoryList = inventoryList
        this.onItemClickListener = onItemCLickListener
    }

    fun updateRecyclerView(){
        notifyDataSetChanged()
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

        val title = inventoryList[position].title
        val price = inventoryList[position].price
        val location = inventoryList[position].location
        val currency = inventoryList[position].currency
        val id = inventoryList[position].id

        if (inventoryList[position].image != null) {
            Glide
                .with(context)
                .asBitmap()
                .load(inventoryList[position].image)
                .into(holder.imageResource)
        } else {
            Glide
                .with(context)
                .asBitmap()
                .load(R.drawable.image_placeholder)
                .into(holder.imageResource)
        }

        holder.inventoryId = id
        holder.titleTextView.text = title
        holder.priceTextView.text = price.toString()
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
        return inventoryList.size
    }
}
