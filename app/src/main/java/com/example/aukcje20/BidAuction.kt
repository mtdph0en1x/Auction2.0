package com.example.aukcje20

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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
            val currentDate = dateFormat.parse(date)

            val price = findViewById<EditText>(R.id.bidding_edittext).text.toString()

            docRef.get().addOnSuccessListener { document ->
                val auction: Auction? = document.toObject(Auction::class.java)

                val aucEnd = dateFormat.format(auction?.auctionEnd)
                val dateEnd = dateFormat.parse(aucEnd)

                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val userUid = currentUser?.uid.toString()

                val docUser = db.collection("users").document(userUid)

                docUser.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()){
                        val username = documentSnapshot.getString("nickname")
                        val startPrice = auction?.startPrice
                        val priceDouble = price.toDoubleOrNull()

                        val newItem = hashMapOf(
                            "uid" to userUid,
                            "nickname" to username,
                            "data" to date,
                            "price" to priceDouble
                        )

                        if (priceDouble != null) {
                            if(priceDouble > startPrice!! ) {
                                if (currentDate != null) {
                                    if(currentDate.after(dateEnd) || currentDate.equals(dateEnd)){
                                        docRef.update("bidders",FieldValue.arrayUnion(newItem))
                                        docRef.update("startPrice", price.toDoubleOrNull())
                                    } else {
                                        Toast.makeText(this, "AUCTION FINISHED", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else
                            {
                                Toast.makeText(this, "TOO SMALL AMOUNT", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}