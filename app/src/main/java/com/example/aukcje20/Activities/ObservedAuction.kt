package com.example.aukcje20.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aukcje20.Adapters.StartAuctionsAdapter
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView

class ObservedAuction : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var auctionList: ArrayList<Auction>
    private lateinit var tempAuctionList: ArrayList<Auction>
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observed_auction)

        recyclerView = findViewById(R.id.rv_observed)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        auctionList = arrayListOf()
        tempAuctionList = arrayListOf()

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


        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Observed auctions"

        getAuctions()

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

    }

    private fun refreshData() {
        auctionList.clear()
        tempAuctionList.clear()

        // Call the function to fetch the updated data
        getAuctions()

        // Complete the refreshing animation
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_action,menu)

        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView

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
    private fun getAuctions() {
        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()
        val currentUserUID = auth.currentUser?.uid.toString()

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.getDefault())
        val date = dateFormat.format(currentTime).orEmpty()
        val currentDate = dateFormat.parse(date)

        val documentRef = db.collection("users").document(currentUserUID)


        documentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val data = document.data
                    val observedArray = data?.get("observed") as? ArrayList<String>

                    if(observedArray != null){
                        db.collection("auctions").get()
                            .addOnSuccessListener {documents ->
                                for(document in documents){
                                    val data = document.toObject(Auction::class.java)
                                    val aucID = data.auctionid
                                    val dateAuction = dateFormat.parse(data.auctionEnd.toString())
                                    if (dateAuction != null) {
                                        if((dateAuction.after(currentDate) || dateAuction.equals(currentDate)) && observedArray.contains(aucID)) {
                                            auctionList.add((data))
                                        }
                                    }
                                }
                                tempAuctionList.addAll(auctionList)

                                val adapter = StartAuctionsAdapter(tempAuctionList)
                                recyclerView.adapter = adapter


                                adapter.setOnItemClickListener(object: StartAuctionsAdapter.onItemClickListener{
                                    override fun onItemClick(position: Int) {
                                        val intent = Intent(this@ObservedAuction,ShowAuction::class.java)
                                        intent.putExtra("UId",auctionList[position].uid)
                                        intent.putExtra("Name",auctionList[position].name)
                                        intent.putExtra("Description",auctionList[position].description)
                                        intent.putExtra("Picture",auctionList[position].imageUrls[1])
                                        intent.putExtra("Price",auctionList[position].startPrice)
                                        intent.putExtra("Auctionid",auctionList[position].auctionid)
                                        intent.putExtra("auctionEnd",auctionList[position].auctionEnd)
                                        startActivity(intent)
                                    }

                                })

                            }
                            .addOnFailureListener{
                                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                            }


                    }

                }
            }
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