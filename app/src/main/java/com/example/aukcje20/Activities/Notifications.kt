package com.example.aukcje20.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aukcje20.Adapters.NotificationsAdapter
import com.example.aukcje20.DataClasses.User
import com.example.aukcje20.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Notifications : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)


        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid.toString()
        val documentRef = db.collection("users").document(userUID)

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
        supportActionBar?.title = "Your notifications"

        val recyclerView = findViewById<RecyclerView>(R.id.rv_notification)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NotificationsAdapter(listOf())


        documentRef.addSnapshotListener { documentSnapshot, _ ->
            val myData = documentSnapshot?.toObject(User::class.java)
            val myDataList = myData?.notifications ?: emptyList()

            val adapter = NotificationsAdapter(myDataList)
            recyclerView.adapter = adapter

            adapter.setOnItemClickListener(object: NotificationsAdapter.onItemClickListener{
                override fun onItemClick(position: Int,itemData: Map<String,Any>) {
                    val intent = Intent(this@Notifications,NotificationShow::class.java)

                    val isChecked = itemData["isChecked"] as Boolean
                    if (!isChecked) {

                        documentRef.get().addOnSuccessListener { documentSnapshot ->
                            val notificationsList = documentSnapshot["notifications"] as List<Map<String, Any>>

                            val updatedNotificationsList = notificationsList.toMutableList()
                            updatedNotificationsList[position] = updatedNotificationsList[position] + ("isChecked" to true)

                            documentRef.update("notifications", updatedNotificationsList)
                        }

                    }

                    intent.putExtra("Header",itemData["header"].toString())
                    intent.putExtra("Text",itemData["information"].toString())
                    startActivity(intent)
                }

            })

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