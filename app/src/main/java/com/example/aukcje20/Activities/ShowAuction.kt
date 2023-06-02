package com.example.aukcje20.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.aukcje20.Adapters.ImageAdapter
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_show_auction.*
import java.util.ArrayList


class ShowAuction : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var aucName: TextView
    private lateinit var aucDescription: TextView
    private lateinit var aucPrice: TextView
    private lateinit var imageUriList: ArrayList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var aucEditButton: Button
    private lateinit var aucBid: Button
    private lateinit var aucEnd: TextView
    private lateinit var auction: Auction
    private lateinit var aucGoBack: ImageButton
    private lateinit var aucObserveButton: ImageButton
    private var db = Firebase.firestore



    @SuppressLint("NotifyDataSetChanged")
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
        aucGoBack = findViewById(R.id.gobackbtn)
        aucObserveButton = findViewById(R.id.observebtn)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Show auction"

        //Implementation of items form MainActivity
        val bundle: Bundle? = intent.extras
        val auctionId = bundle?.getString("Auctionid")


        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid.toString()
        val documentRef = db.collection("users").document(userUID)

        //NavigationView

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // Handle Home
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
                R.id.nav_notifications ->{
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

                if (currentUser != null && auction.uid == currentUser.uid) {
                    aucEditButton.visibility = View.VISIBLE
                } else {
                    aucEditButton.visibility = View.GONE
                }
            }

        documentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val data = document.data
                    val observedArray = data?.get("observed") as? ArrayList<String>

                    // Check if the array contains the data ID
                    if (observedArray != null && observedArray.contains(auctionId)) {
                        aucObserveButton.setImageResource(R.drawable.eye_close_1)
                    } else {
                        aucObserveButton.setImageResource(R.drawable.baseline_observe_eye_24)
                    }
                }
            }
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {


            db.collection("auctions")
                .document(auctionId)
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

                    if (currentUser != null && auction.uid == currentUser.uid) {
                        aucEditButton.visibility = View.VISIBLE
                    } else {
                        aucEditButton.visibility = View.GONE
                    }
                }

            swipeRefreshLayout.isRefreshing = false
        }


        aucGoBack.setOnClickListener {
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
                                    observedArray.add(auctionId)
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


            aucEditButton.setOnClickListener {
            val intent = Intent(this, EditAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            startActivity(intent)
        }

        aucBid.setOnClickListener{
            val intent = Intent(this, BidAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            startActivity(intent)
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