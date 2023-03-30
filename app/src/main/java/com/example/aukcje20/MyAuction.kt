package com.example.aukcje20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class MyAuction : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var auctionList: ArrayList<Auction>
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_auction)

        recyclerView = findViewById(R.id.rvMyAuciton)
        recyclerView.layoutManager = GridLayoutManager(this,2)

        auctionList = arrayListOf()


        getAuctions()


    }

    private fun getAuctions() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        db.collection("auctions").get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val auction:Auction? = data.toObject(Auction::class.java)
                        if(auction!=null && auction.uid == auth.currentUser?.uid)
                        {
                            auctionList.add((auction))
                        }
                    }

                    val adapter = StartAuctionsAdapter(auctionList)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object: StartAuctionsAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            //Toast.makeText(this@MainActivity,"STH $position",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MyAuction,ShowAuction::class.java)
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
}