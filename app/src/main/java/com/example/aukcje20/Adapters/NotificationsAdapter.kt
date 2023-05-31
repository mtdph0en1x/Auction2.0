package com.example.aukcje20.Adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aukcje20.R


class NotificationsAdapter(private val notifications: List<Map<String, Any>>) :
    RecyclerView.Adapter<NotificationsAdapter.MyDataViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int, itemData: Map<String, Any>)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification, parent, false)
        return MyDataViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyDataViewHolder, position: Int) {
        val myData = notifications[position]
        holder.bind(myData)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    inner class MyDataViewHolder(itemView: View, private val listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        private val headerTextView: TextView = itemView.findViewById(R.id.tv_notification_header)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val itemData = notifications[position]
                    listener.onItemClick(position, itemData)
                }
            }
        }

        fun bind(itemData: Map<String, Any>) {
            headerTextView.text = itemData["header"].toString()
            val isChecked = itemData["isChecked"] as Boolean

            val textStyle = if (isChecked) Typeface.NORMAL else Typeface.BOLD
            val textColor = if (isChecked) androidx.appcompat.R.color.abc_hint_foreground_material_light else R.color.black

            headerTextView.setTypeface(null, textStyle)
            headerTextView.setTextColor(ContextCompat.getColor(itemView.context, textColor))

        }
    }
}