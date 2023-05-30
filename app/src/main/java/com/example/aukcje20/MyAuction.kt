package com.example.aukcje20

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyAuction : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var auctionList: ArrayList<Auction>
    private lateinit var tempAuctionList: ArrayList<Auction>
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_auction)

        recyclerView = findViewById(R.id.rvMyAuciton)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        auctionList = arrayListOf()
        tempAuctionList = arrayListOf()

        getAuctions()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_action, menu)

        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(p0: String?): Boolean {

                tempAuctionList.clear()
                val searchText = p0!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    auctionList.forEach {
                        if (it.name?.lowercase(Locale.getDefault())?.contains(searchText) == true) {
                            tempAuctionList.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()

                } else {

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

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(currentTime).orEmpty()
        val currentDate = dateFormat.parse(date)

        auth = FirebaseAuth.getInstance()
        val currentUserUID = auth.currentUser?.uid.toString()

        db.collection("auctions").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = document.toObject(Auction::class.java)
                    val dateAuction = dateFormat.parse(data.auctionEnd)
                    if ((dateAuction.after(currentDate) || dateAuction.equals(currentDate)) && data.uid == currentUserUID) {
                        auctionList.add((data))
                    }
                }
                tempAuctionList.addAll(auctionList)

                val adapter = StartAuctionsAdapter(tempAuctionList)
                recyclerView.adapter = adapter


                adapter.setOnItemClickListener(object : StartAuctionsAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = Intent(this@MyAuction, ShowAuction::class.java)
                        intent.putExtra("UId", auctionList[position].uid)
                        intent.putExtra("Name", auctionList[position].name)
                        intent.putExtra("Description", auctionList[position].description)
                        intent.putExtra("Picture", auctionList[position].imageUrl)
                        intent.putExtra("Price", auctionList[position].startPrice)
                        intent.putExtra("Auctionid", auctionList[position].auctionid)
                        intent.putExtra("auctionEnd", auctionList[position].auctionEnd)
                        startActivity(intent)
                    }

                })

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            }
    }
}