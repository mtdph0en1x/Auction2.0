package com.example.aukcje20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BidAuction : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid_auction)

        val bundle: Bundle? = intent.extras
        val auctionId = bundle!!.getString("AuctionID")
        val docRef = db.collection("auctions").document(auctionId.toString())

        // Set up the RecyclerView with the custom adapter
        val recyclerView = findViewById<RecyclerView>(R.id.bidding_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BiddingAdapter(listOf())

        // Load the data from Firestore and update the RecyclerView adapter
        docRef.addSnapshotListener { documentSnapshot, _ ->
            val myData = documentSnapshot?.toObject(Auction::class.java)
            val myDataList = myData?.bidders ?: emptyList()
            recyclerView.adapter = BiddingAdapter(myDataList)
        }

    }
}