package com.example.aukcje20

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_auction.*

class EditAuction : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auction: Auction
    private lateinit var updatedAuction: Auction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_auction)

        // Get the auction ID from the intent extras
        val bundle: Bundle? = intent.extras
        val auctionId = bundle!!.getString("AuctionID")

        // Retrieve the auction object from Firestore

        db.collection("auctions")
            .document(auctionId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                auction = documentSnapshot.toObject(Auction::class.java)!!

                // Populate the UI with auction data
                editTextName.setText(auction.name)
                editTextDescription.setText(auction.description)
                editTextStartPrice.setText(auction.startPrice.toString())
                Picasso.get().load(auction.imageUrl).into(aucPic)

                // Set a click listener on the "Edit" button to update the auction data in Firestore
                button_update_auction.setOnClickListener {
                    val name = editTextName.text.toString()
                    val description = editTextDescription.text.toString()
                    val startPrice = editTextStartPrice.text.toString().toDoubleOrNull()

                    if (name.isBlank() || description.isBlank() || startPrice == null || startPrice <= 0) {
                        Toast.makeText(this, "Please fill in all fields with valid values", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Update the auction object with the new values
                    updatedAuction = Auction(auction.auctionid, auction.uid, name, description, startPrice, 8.1, auction.imageUrl,auction.auctionEnd)


                    db.collection("auctions")
                        .document(auctionId)
                        .set(updatedAuction)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Auction updated successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error updating auction: $exception", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error retrieving auction: $exception", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
