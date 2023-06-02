package com.example.aukcje20.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.aukcje20.Adapters.BiddingAdapter
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.google.android.material.navigation.NavigationView
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var aucGoBack: ImageButton
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid_auction)

        val bundle: Bundle? = intent.extras
        val auctionId = bundle!!.getString("AuctionID")

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Main menu"


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
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userUID = currentUser?.uid.toString()
                val dateAuction = dateFormat.parse(auction?.auctionEnd.toString())

                val docUser = db.collection("users").document(userUID)

                docUser.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()){
                        val username = documentSnapshot.getString("nickname")
                        val startPrice = auction?.startPrice
                        val priceDouble = price.toDoubleOrNull()

                        val newItem = hashMapOf(
                            "nickname" to username,
                            "data" to date,
                            "price" to priceDouble
                        )

                        if (priceDouble != null) {
                            if(priceDouble > startPrice!! ) {
                                if (currentDate != null) {
                                    if(currentDate < dateAuction){
                                        docRef.update("bidders",FieldValue.arrayUnion(newItem))
                                            .addOnSuccessListener {
                                                //Toast.makeText(this, "WE DID IT", Toast.LENGTH_SHORT).show()
                                            }
                                        docRef.update("startPrice", price.toDoubleOrNull()).addOnSuccessListener {
                                            //Toast.makeText(this, "WE DID IT AGAIN", Toast.LENGTH_SHORT).show()
                                        }
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
                    else
                    {
                        Toast.makeText(this, "DO NOT WORK USERNAME", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

        aucGoBack = findViewById(R.id.gobackbtn)
        aucGoBack.setOnClickListener {
            this.finish()
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {

            docRef.addSnapshotListener { documentSnapshot, _ ->
                val myData = documentSnapshot?.toObject(Auction::class.java)
                val myDataList = myData?.bidders ?: emptyList()
                recyclerView.adapter = BiddingAdapter(myDataList.reversed())
            }

            swipeRefreshLayout.isRefreshing = false
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