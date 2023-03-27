package com.example.aukcje20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class ShowAuction : AppCompatActivity() {

    private lateinit var aucName: TextView
    private lateinit var aucDescription: TextView
    private lateinit var aucPrice:TextView
    private lateinit var aucPicture:ImageView
    private lateinit var aucGoBack:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_auction)


        //Implementation of items in activity
        aucName = findViewById(R.id.auc_tv_name)
        aucDescription = findViewById(R.id.auc_tv_description)
        aucPrice = findViewById(R.id.auc_tv_price)
        aucPicture = findViewById(R.id.auc_iv_picture)
        aucGoBack = findViewById(R.id.gobackbtn)

        //Implementation of items form MainActivity
        val bundle : Bundle? = intent.extras
        val name = bundle!!.getString("Name")
        val dsc = bundle.getString("Description")
        val image = bundle.getString("Picture")
        val priceS = "${bundle.getDouble("Price")} $"

        //Setting values
        aucName.text = name
        aucDescription.text = dsc
        Picasso.get().load(image).into(aucPicture)
        aucPrice.text = priceS

        //Button which enables to go back to main activity
        aucGoBack.setOnClickListener{
            this.finish()
        }

    }
}