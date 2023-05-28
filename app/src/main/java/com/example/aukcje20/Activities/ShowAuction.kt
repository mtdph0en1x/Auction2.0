package com.example.aukcje20.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.aukcje20.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class ShowAuction : AppCompatActivity() {

    private lateinit var aucName: TextView
    private lateinit var aucDescription: TextView
    private lateinit var aucPrice: TextView
    private lateinit var aucPicture: ImageView
    private lateinit var aucGoBack: ImageButton
    private lateinit var aucEditButton: Button
    private lateinit var aucBid: Button
    private lateinit var aucEnd: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_auction)

        //Implementation of items in activity
        aucName = findViewById(R.id.auc_tv_name)
        aucDescription = findViewById(R.id.auc_tv_description)
        aucPrice = findViewById(R.id.auc_tv_price)
        aucPicture = findViewById(R.id.auc_iv_picture)
        aucGoBack = findViewById(R.id.gobackbtn)
        aucEditButton = findViewById(R.id.EditButton)
        aucBid = findViewById(R.id.show_auction_bid_button)
        aucEnd = findViewById(R.id.tv_date_auction_end)

        //Implementation of items form MainActivity
        val bundle: Bundle? = intent.extras
        val name = bundle!!.getString("Name")
        val dsc = bundle.getString("Description")
        val image = bundle.getString("Picture")
        val priceS = "${bundle.getDouble("Price")} $"
        val uid = bundle.getString("UId")
        val auctionId = bundle.getString("Auctionid")
        val auctionEnd = bundle.getString("auctionEnd")

        //Setting values
        aucName.text = name
        aucDescription.text = dsc
        Picasso.get().load(image).into(aucPicture)
        aucPrice.text = priceS
        aucEnd.text = auctionEnd

        //Button which enables to go back to main activity
        aucGoBack.setOnClickListener{
            this.finish()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && uid == currentUser.uid) {
            aucEditButton.visibility = View.VISIBLE
        } else {
            aucEditButton.visibility = View.GONE
        }


        aucEditButton.setOnClickListener {
            val intent = Intent(this, EditAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            startActivity(intent)
        }

        aucBid.setOnClickListener{
            val intent = Intent(this, BidAuction::class.java)
            intent.putExtra("AuctionID", auctionId)
            intent.putExtra("auctionEnd",auctionEnd)
            startActivity(intent)
        }

    }
}