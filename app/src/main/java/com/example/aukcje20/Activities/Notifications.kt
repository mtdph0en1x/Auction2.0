package com.example.aukcje20.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aukcje20.Adapters.NotificationsAdapter
import com.example.aukcje20.DataClasses.User
import com.example.aukcje20.R
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


}