package com.example.aukcje20

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var auctionList: ArrayList<Auction>
    private var db = Firebase.firestore


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

        auctionList = arrayListOf()

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

                else -> false
            }
        }

        // Set up the drawer toggle
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun getAuctions() {
        db = FirebaseFirestore.getInstance()
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

                    var adapter = StartAuctionsAdapter(auctionList)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object: StartAuctionsAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            //Toast.makeText(this@MainActivity,"STH $position",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity,ShowAuction::class.java)
                            intent.putExtra("Name",auctionList[position].name)
                            intent.putExtra("Description",auctionList[position].description)
                            intent.putExtra("Picture",auctionList[position].imageUrl)
                            intent.putExtra("Price",auctionList[position].startPrice)
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