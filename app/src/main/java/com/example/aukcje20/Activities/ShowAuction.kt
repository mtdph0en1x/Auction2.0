package com.example.aukcje20.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aukcje20.Adapters.ImageAdapter
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_auction.*
import java.util.ArrayList


class ShowAuction : AppCompatActivity() {

    private lateinit var aucName: TextView
    private lateinit var aucDescription: TextView
    private lateinit var aucPrice: TextView
    private lateinit var imageUriList: ArrayList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var aucEditButton: Button
    private lateinit var aucBid: Button
    private lateinit var aucEnd: TextView
    private lateinit var auction: Auction
    private var db = Firebase.firestore





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_auction)
        db = FirebaseFirestore.getInstance()


        //Implementation of items in activity
        aucName = findViewById(R.id.auc_tv_name)
        aucDescription = findViewById(R.id.auc_tv_description)
        aucPrice = findViewById(R.id.auc_tv_price)
        aucEditButton = findViewById(R.id.EditButton)
        aucBid = findViewById(R.id.show_auction_bid_button)
        aucEnd = findViewById(R.id.tv_date_auction_end)


        //Implementation of items form MainActivity
        val bundle: Bundle? = intent.extras
        val auctionId = bundle?.getString("Auctionid")

        // Initialize the RecyclerView and ImageAdapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageUriList = ArrayList()
        var imageAdapter: ImageAdapter = ImageAdapter(imageUriList)
        recyclerView.adapter = imageAdapter

        db.collection("auctions")
            .document(auctionId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                auction = documentSnapshot.toObject(Auction::class.java)!!

                //Setting values
                aucName.text = auction.name
                aucDescription.text = auction.description
                imageUriList.clear()
                imageUriList.addAll(auction.imageUrls)
                aucPrice.text = auction.startPrice.toString()
                aucEnd.text = auction.auctionEnd

                imageAdapter.notifyDataSetChanged()

                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null && auction.uid == currentUser.uid) {
                    aucEditButton.visibility = View.VISIBLE
                } else {
                    aucEditButton.visibility = View.GONE
                }
            }








        aucEditButton.setOnClickListener {
            val intent = Intent(this, EditAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            startActivity(intent)
        }

        aucBid.setOnClickListener{
            val intent = Intent(this, BidAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            intent.putExtra("auctionEnd",auction.auctionEnd)
            startActivity(intent)
        }

    }
}