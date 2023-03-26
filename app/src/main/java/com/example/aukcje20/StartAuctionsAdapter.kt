package com.example.aukcje20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class StartAuctionsAdapter(private val auctionList: ArrayList<Auction>) : RecyclerView.Adapter<StartAuctionsAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvImage: ImageView = itemView.findViewById(R.id.Picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return auctionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = auctionList[position].name
        holder.tvPrice.text = auctionList[position].startPrice.toString()
        //Glide.with(this).load(auctionList[position].imageUrl).into()
        Picasso.get().load(auctionList[position].imageUrl).into(holder.tvImage)
    }
}




