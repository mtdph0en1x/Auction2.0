package com.example.aukcje20.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.aukcje20.Adapters.StartAuctionsAdapter
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var auctionList: ArrayList<Auction>
    private lateinit var tempAuctionList: ArrayList<Auction>
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Find the RefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        // Find the DrawerLayout
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        // Find the NavigationView
        val navView: NavigationView = findViewById(R.id.nav_view)

        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.rv_main)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        //Initialization of list of Auctions
        auctionList = arrayListOf()

        //Necessary to create temporary list of Auctions thanks to searchView
        tempAuctionList = arrayListOf()

        //Function which enables to show list of Auctions
        getAuctions()

        // Set up the navigation drawer menu
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

        // Set up the drawer toggle
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_action,menu)

        val itemSearch = menu?.findItem(R.id.search)
        val searchView = itemSearch?.actionView as SearchView

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Main menu"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(p0: String?): Boolean {

                tempAuctionList.clear()
                val searchText = p0!!.lowercase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    auctionList.forEach{
                        if(it.name?.lowercase(Locale.getDefault())?.contains(searchText) == true){
                            tempAuctionList.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }else{
                    tempAuctionList.clear()
                    tempAuctionList.addAll(auctionList)
                    recyclerView.adapter!!.notifyDataSetChanged()

                }

                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getAuctions() {
        db = FirebaseFirestore.getInstance()

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.getDefault())
        val date = dateFormat.format(currentTime).orEmpty()
        val currentDate = dateFormat.parse(date)

        db.collection("auctions").get()
            .addOnSuccessListener { querySnapshot ->
                val auctionList: ArrayList<Auction> = ArrayList()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(Auction::class.java)
                    if (data != null)
                    {
                        val dateAuction = dateFormat.parse(data.auctionEnd.toString())
                        if (dateAuction != null) {
                            if(dateAuction.after(currentDate) || dateAuction.equals(currentDate)) {
                                auctionList.add((data))
                            } else {
                                if(!data.duplication!!) {
                                    data.duplication = true
                                    db.collection("auctions").document(data.auctionid.toString()).update("duplication",data.duplication)
                                    if (data.bidders.isNotEmpty()) {
                                        val arrayLast = data.bidders.last()
                                        data.winnerId = arrayLast["uid"] as String?
                                        db.collection("auctions").document(data.auctionid.toString()).update("winnerId",data.winnerId.toString())

                                        db.collection("users").document(data.winnerId.toString()).get().addOnSuccessListener {
                                            val notificationID = UUID.randomUUID().toString().trim()
                                            val headerText = "You've won an auction: ${data.name}!"
                                            val infoText = "Welcome! \n" +
                                                    "We are happy to inform you that you've won an auction ${data.name} for ${data.startPrice}$." +
                                                    "We would like you to inform that you have to send an correct price on our bank account: PL20109024026388698431595484\n" +
                                                    "Auction2.0"
                                            val isChecked = false
                                            val newItem = hashMapOf(
                                                "id" to notificationID,
                                                "header" to headerText,
                                                "information" to infoText,
                                                "isChecked" to isChecked
                                            )
                                            db.collection("users").document(data.winnerId.toString()).update("notifications", FieldValue.arrayUnion(newItem))

                                        }

                                        db.collection("users").document(data.uid.toString()).get().addOnSuccessListener {
                                            val winnerNickname = arrayLast["nickname"] as String?

                                            val notificationID = UUID.randomUUID().toString().trim()
                                            val headerText = "Your auction has ended"
                                            val infoText = "Welcome! \n" +
                                                    "We are happy to inform you that your auction ${data.name} has ended!" +
                                                    "The highest bid goes to $winnerNickname for ${data.startPrice}$!\n" +
                                                    "Auction2.0"
                                            val isChecked = false
                                            val newItem = hashMapOf(
                                                "id" to notificationID,
                                                "header" to headerText,
                                                "information" to infoText,
                                                "isChecked" to isChecked
                                            )
                                            db.collection("users").document(data.uid.toString()).update("notifications", FieldValue.arrayUnion(newItem))
                                        }


                                    } else {
                                        db.collection("users").document(data.uid.toString()).get().addOnSuccessListener {

                                            val notificationID = UUID.randomUUID().toString().trim()
                                            val headerText = "Your auction has ended"
                                            val infoText = "Welcome! \n" +
                                                    "We are sorry to inform you that your auction ${data.name} has ended without any bid"
                                            val isChecked = false
                                            val newItem = hashMapOf(
                                                "id" to notificationID,
                                                "header" to headerText,
                                                "information" to infoText,
                                                "isChecked" to isChecked
                                            )
                                            db.collection("users").document(data.uid.toString())
                                                .update("notifications", FieldValue.arrayUnion(newItem))
                                        }
                                    }

                                }

                            }
                        }
                    }


                }

                val tempAuctionList: ArrayList<Auction> = ArrayList()
                tempAuctionList.addAll(auctionList)

                val adapter = StartAuctionsAdapter(tempAuctionList)
                recyclerView.adapter = adapter

                adapter.setOnItemClickListener(object : StartAuctionsAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = Intent(this@MainActivity, ShowAuction::class.java)
                        intent.putExtra("UId", auctionList[position].uid)
                        intent.putExtra("Name", auctionList[position].name)
                        intent.putExtra("Description", auctionList[position].description)
                        intent.putExtra("Picture", auctionList[position].imageUrls[1])
                        intent.putExtra("Price", auctionList[position].startPrice)
                        intent.putExtra("Auctionid", auctionList[position].auctionid)
                        intent.putExtra("auctionEnd", auctionList[position].auctionEnd)
                        startActivity(intent)
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting auctions: $exception")
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

    private fun refreshData() {
        // Clear the existing data
        auctionList.clear()
        tempAuctionList.clear()

        // Call the function to fetch the updated data
        getAuctions()

        // Complete the refreshing animation
        swipeRefreshLayout.isRefreshing = false
    }
}


