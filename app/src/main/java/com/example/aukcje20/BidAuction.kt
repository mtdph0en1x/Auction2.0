package com.example.aukcje20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class BidAuction : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var bidSubmit: Button

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
            val dateFormat = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
            val date = dateFormat.format(currentTime).orEmpty()

            val price = findViewById<EditText>(R.id.bidding_edittext).text.toString()


            docRef.get().addOnSuccessListener { document ->
                val auction: Auction? = document.toObject(Auction::class.java)
                val uid = auction?.uid


                val newItem = hashMapOf(
                    "uid" to uid,
                    "data" to date,
                    "price" to price
                )

                    docRef.update("bidders", FieldValue.arrayUnion(newItem))
                        .addOnSuccessListener {
                            Toast.makeText(this, "WE DID IT", Toast.LENGTH_SHORT).show()
                        }

                    docRef.update("startPrice", price.toDoubleOrNull()).addOnSuccessListener {
                        Toast.makeText(this, "WE DID IT AGAIN", Toast.LENGTH_SHORT).show()
                    }

            }

        }

    }

    /*private fun onAddButtonClick() {
        val name = findViewById<EditText>(R.id.nameEditText).text.toString()
        val age = findViewById<EditText>(R.id.ageEditText).text.toString().toInt()


        // Get the current data from the Firestore document
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val myData = documentSnapshot.toObject(MyDataClass::class.java)

            // Create a new list with the existing items and the new item
            val updatedList = myData.myArray.toMutableList().apply {
                add(mapOf("name" to name, "age" to age))
            }

            // Create a new instance of MyDataClass with the updated list
            val updatedData = MyDataClass(updatedList)

            // Set the value of the Firestore document to the updated data
            docRef.set(updatedData)
        }
    }*/
}