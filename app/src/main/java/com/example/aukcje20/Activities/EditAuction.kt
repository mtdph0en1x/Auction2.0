package com.example.aukcje20.Activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.google.android.material.navigation.NavigationView
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

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    // Handle Profile
                    true
                }
                R.id.nav_settings -> {
                    // Handle Settings
                    true
                }
                R.id.nav_new_auction -> {
                    val intent = Intent(this, NewAuction::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_my_auction -> {
                    val intent = Intent(this, MyAuction::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_observed -> {
                    val intent = Intent(this, ObservedAuction::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_notifications -> {
                    val intent = Intent(this, Notifications::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Main menu"


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
                Picasso.get().load(auction.imageUrls[1]).into(aucPic)

                // Set a click listener on the "Edit" button to update the auction data in Firestore
                button_update_auction.setOnClickListener {
                    val name = editTextName.text.toString()
                    val description = editTextDescription.text.toString()
                    val startPrice = editTextStartPrice.text.toString().toDoubleOrNull()

                    if (name.isBlank() || description.isBlank() || startPrice == null || startPrice <= 0) {
                        Toast.makeText(
                            this,
                            "Please fill in all fields with valid values",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    // Update the auction object with the new values
                    updatedAuction = Auction(
                        auction.auctionid,
                        auction.uid,
                        name,
                        description,
                        startPrice,
                        8.1,
                        auction.imageUrls,
                        auction.auctionEnd,
                        auction.winnerId,
                        auction.duplication
                    )


                    db.collection("auctions")
                        .document(auctionId)
                        .set(updatedAuction)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Auction updated successfully", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this,
                                "Error updating auction: $exception",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error retrieving auction: $exception", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        return if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
            drawerLayout.closeDrawer(findViewById(R.id.nav_view))
            true
        } else {
            super.onSupportNavigateUp()
        }
    }
}
