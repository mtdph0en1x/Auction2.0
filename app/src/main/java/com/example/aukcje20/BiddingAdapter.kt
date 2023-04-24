package com.example.aukcje20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BiddingAdapter(private val bidders: List<Map<String, Any>>) :
    RecyclerView.Adapter<BiddingAdapter.MyDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bid, parent, false)
        return MyDataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyDataViewHolder, position: Int) {
        val myData = bidders[position]
        holder.uidTextView.text = myData["uid"].toString()
        holder.dataTextView.text = myData["data"].toString()
        holder.priceTextView.text = myData["price"].toString()
    }

    override fun getItemCount() = bidders.size

    class MyDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val uidTextView: TextView = itemView.findViewById(R.id.bid_uid)
        val dataTextView: TextView = itemView.findViewById(R.id.bid_data)
        val priceTextView: TextView = itemView.findViewById(R.id.bid_price)
    }
}
