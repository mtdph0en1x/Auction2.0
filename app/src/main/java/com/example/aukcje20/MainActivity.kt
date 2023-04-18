package com.example.aukcje20

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var auctionList: ArrayList<Auction>
    private lateinit var tempAuctionList: ArrayList<Auction>
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the DrawerLayout
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        // Find the NavigationView
        val navView: NavigationView = findViewById(R.id.nav_view)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this,2)

        //Initialization of list of Auctions
        auctionList = arrayListOf<Auction>()

        //Necessary to create temporary list of Auctions thanks to searchView
        tempAuctionList = arrayListOf<Auction>()

        //Function which enables to show list of Auctions
        getAuctions()

        // Set up the navigation drawer menu
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // Handle Home
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
                R.id.nav_my_auction ->{
                    val intent = Intent(this, MyAuction::class.java)
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

        db.collection("auctions").get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val auction:Auction? = data.toObject(Auction::class.java)
                        if(auction!=null)
                        {
                            auctionList.add((auction))
                        }
                    }

                    tempAuctionList.addAll(auctionList)

                    val adapter = StartAuctionsAdapter(tempAuctionList)
                    recyclerView.adapter = adapter


                    adapter.setOnItemClickListener(object: StartAuctionsAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            //Toast.makeText(this@MainActivity,"STH $position",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity,ShowAuction::class.java)
                            intent.putExtra("UId",auctionList[position].uid)
                            intent.putExtra("Name",auctionList[position].name)
                            intent.putExtra("Description",auctionList[position].description)
                            intent.putExtra("Picture",auctionList[position].imageUrl)
                            intent.putExtra("Price",auctionList[position].startPrice)
                            intent.putExtra("Auctionid",auctionList[position].auctionid)
                            startActivity(intent)
                        }

                    })

                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            }
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


