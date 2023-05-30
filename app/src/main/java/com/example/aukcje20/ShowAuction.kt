package com.example.aukcje20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ShowAuction : AppCompatActivity() {

    private val db = Firebase.firestore



    private lateinit var aucName: TextView
    private lateinit var aucDescription: TextView
    private lateinit var aucPrice: TextView
    private lateinit var aucPicture: ImageView
    private lateinit var aucGoBack: ImageButton
    private lateinit var aucObserveButton: ImageButton
    private lateinit var aucEditButton: Button
    private lateinit var aucBid: Button
    private lateinit var aucEnd: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_auction)

        //Implementation of items in activity
        aucName = findViewById(R.id.auc_tv_name)
        aucDescription = findViewById(R.id.auc_tv_description)
        aucPrice = findViewById(R.id.auc_tv_price)
        aucPicture = findViewById(R.id.auc_iv_picture)
        aucGoBack = findViewById(R.id.gobackbtn)
        aucEditButton = findViewById(R.id.EditButton)
        aucObserveButton = findViewById(R.id.observebtn)
        aucBid = findViewById(R.id.show_auction_bid_button)
        aucEnd = findViewById(R.id.tv_date_auction_end)


        //Implementation of items form MainActivity
        val bundle: Bundle? = intent.extras
        val name = bundle!!.getString("Name")
        val dsc = bundle.getString("Description")
        val image = bundle.getString("Picture")
        val priceS = "${bundle.getDouble("Price")} $"
        val uid = bundle.getString("UId")
        val auctionId = bundle.getString("Auctionid")
        val auctionEnd = bundle.getString("auctionEnd")


        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid.toString()

        val documentRef = db.collection("users").document(userUID)

        //Setting values
        aucName.text = name
        aucDescription.text = dsc
        Picasso.get().load(image).into(aucPicture)
        aucPrice.text = priceS
        aucEnd.text = auctionEnd

        documentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val data = document.data
                    val observedArray = data?.get("observed") as? ArrayList<String>

                    // Check if the array contains the data ID
                    if (observedArray != null && observedArray.contains(auctionId)) {
                       // aucObserveButton.setImageResource(R.drawable.baseline_observe_eye_24)
                        aucObserveButton.setImageResource(R.drawable.eye_close_1)
                    } else {
                       // aucObserveButton.setImageResource(R.drawable.eye_close_1)
                        aucObserveButton.setImageResource(R.drawable.baseline_observe_eye_24)
                    }
                }
            }
        }

        //Button which enables to go back to main activity
        aucGoBack.setOnClickListener{
            this.finish()
        }

        aucObserveButton.setOnClickListener{
            documentRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val data = document.data
                        val observedArray = data?.get("observed") as? ArrayList<String>

                        // Check if the array contains the data ID
                        if (observedArray != null && observedArray.contains(auctionId)) {
                            // The data ID already exists in the array
                            observedArray.remove(auctionId)

                            // Update the document with the modified array
                            documentRef.update("observed", observedArray)
                                .addOnCompleteListener {}

                            aucObserveButton.setImageResource(R.drawable.baseline_observe_eye_24)

                        } else {
                            // Add the new data ID to the array
                            if (observedArray != null) {
                                if (auctionId != null) {
                                    observedArray.add(auctionId)
                                }
                            } else {
                                val newArray = arrayListOf(auctionId)
                                data?.put("observed", newArray)
                            }
                            // Update the document with the modified array
                            documentRef.set(data!!)
                                .addOnCompleteListener {
                                }

                            aucObserveButton.setImageResource(R.drawable.eye_close_1)
                        }
                    }
                }
            }
        }

        if (currentUser != null && uid == currentUser.uid) {
            aucEditButton.visibility = View.VISIBLE
        } else {
            aucEditButton.visibility = View.GONE
        }


        aucEditButton.setOnClickListener {
            val intent = Intent(this, EditAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            startActivity(intent)
        }

        aucBid.setOnClickListener{
            val intent = Intent(this, BidAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            intent.putExtra("auctionEnd",auctionEnd)
            startActivity(intent)
        }

    }

}