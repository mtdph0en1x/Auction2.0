package com.example.aukcje20.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aukcje20.Adapters.BiddingAdapter
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*


class BidAuction : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var bidSubmit: Button

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid_auction)

        val bundle: Bundle? = intent.extras
        val auctionId = bundle!!.getString("AuctionID")
        val auctionEnd = bundle.getString("auctionEnd").toString()

        val docRef = db.collection("auctions").document(auctionId.toString())

        bidSubmit = findViewById(R.id.bidding_button_submit)

        // Set up the RecyclerView with the custom adapter
        val recyclerView = findViewById<RecyclerView>(R.id.bidding_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BiddingAdapter(listOf())

        // Load the data from Firestore and update the RecyclerView adapter
        docRef.addSnapshotListener { documentSnapshot, _ ->
            val myData = documentSnapshot?.toObject(Auction::class.java)
            val myDataList = myData?.bidders ?: emptyList()
            recyclerView.adapter = BiddingAdapter(myDataList.reversed())
        }

        bidSubmit.setOnClickListener{

            val currentTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.getDefault())
            val date = dateFormat.format(currentTime).orEmpty()

            val price = findViewById<EditText>(R.id.bidding_edittext).text.toString()

            docRef.get().addOnSuccessListener { document ->
                val auction: Auction? = document.toObject(Auction::class.java)
                val uid = auction?.uid

                val docUser = db.collection("users").document(uid.toString())

                docUser.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()){
                        val username = documentSnapshot.getString("nickname")
                        val startPrice = auction?.startPrice
                        val priceDouble = price.toDoubleOrNull()

                        val newItem = hashMapOf(
                            "uid" to username,
                            "data" to date,
                            "price" to priceDouble
                        )

                        if (priceDouble != null) {
                            if(priceDouble > startPrice!! ) {
                                if(date < auctionEnd){
                                    docRef.update("bidders",FieldValue.arrayUnion(newItem))
                                        .addOnSuccessListener {
                                            //Toast.makeText(this, "WE DID IT", Toast.LENGTH_SHORT).show()
                                        }
                                    docRef.update("startPrice", price.toDoubleOrNull()).addOnSuccessListener {
                                        //Toast.makeText(this, "WE DID IT AGAIN", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else
                                {
                                    Toast.makeText(this, "AUCTION FINISHED", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else
                            {
                                Toast.makeText(this, "TOO SMALL AMOUNT", Toast.LENGTH_SHORT).show()
                            }
                        }


                    }
                    else
                    {
                        Toast.makeText(this, "DO NOT WORK USERNAME", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }
}