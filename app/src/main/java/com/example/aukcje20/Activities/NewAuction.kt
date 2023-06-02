@file:Suppress("DEPRECATION")

package com.example.aukcje20.Activities


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aukcje20.Adapters.ImageAdapter
import com.example.aukcje20.DataClasses.Auction
import com.example.aukcje20.R
import com.example.aukcje20.databinding.ActivityNewAuctionBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class NewAuction : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private val MAX_PHOTOS = 5
    private lateinit var imageUriList: ArrayList<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var storageRef: StorageReference

    private lateinit var binding: ActivityNewAuctionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAuctionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.imagesView


        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        imageUriList = ArrayList()
        val imageAdapter = ImageAdapter(imageUriList)
        recyclerView.adapter = imageAdapter

        auth = FirebaseAuth.getInstance()

        val nameEditText = binding.editTextName
        val descriptionEditText = binding.editTextDescription
        val startPriceEditText = binding.editTextStartPrice
        val buyNowPriceEditText = binding.editTextBuyNowPrice
        val addPhotoButton = binding.buttonAddPhoto
        val createAuctionButton = binding.buttonCreateAuction
        val addDateButton = binding.buttonAddDate
        val photoText = binding.tvAddPhoto

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
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
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
        supportActionBar?.title = "Create new Auction"

        addPhotoButton.setOnClickListener { openFileChooser() }

        val myCalendar = Calendar.getInstance()

        val datePicker =
            DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val timePicker = TimePickerDialog(
                    this,
                    { view: TimePicker, hourOfDay: Int, minute: Int ->
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        myCalendar.set(Calendar.MINUTE, minute)
                        myCalendar.set(Calendar.SECOND, 0)

                        val myFormat = "dd-MM-yyyy HH:mm:ss"
                        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                        photoText.text = sdf.format(myCalendar.time)
                    },
                    0,
                    0,
                    true
                )
                timePicker.show()
            }

        addDateButton.setOnClickListener {
            DatePickerDialog(
                this, datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH),
            )
                .show()
        }

        createAuctionButton.setOnClickListener {
            createAuction(
                UUID.randomUUID().toString().trim(),
                auth.currentUser?.uid.toString().trim(),
                nameEditText.text.toString().trim(),
                descriptionEditText.text.toString().trim(),
                startPriceEditText.text.toString().toDoubleOrNull(),
                buyNowPriceEditText.text.toString().toDoubleOrNull(),
                photoText.text.toString()
            )
        }

        // Create a reference to the Firebase Storage instance
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
    }

    private fun openFileChooser() {
        if (imageUriList.size >= MAX_PHOTOS) {
            Toast.makeText(
                this,
                "Maximum $MAX_PHOTOS photos can be added.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val clipData = data.clipData
            if (clipData != null) {
                // Multiple images selected
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    imageUriList.add(imageUri.toString())
                }
            } else {
                // Single image selected
                val imageUri = data.data
                imageUriList.add(imageUri!!.toString())
            }

            this.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun createAuction(
        auctionId: String,
        uid: String,
        name: String,
        description: String,
        startPrice: Double?,
        buyNowPrice: Double?,
        auctionEnd: String
    ) {
        if (imageUriList.isEmpty()) {
            Toast.makeText(this, "Please select at least one image.", Toast.LENGTH_SHORT).show()
            return
        }

        val auction = Auction(
            auctionId,
            uid,
            name,
            description,
            startPrice,
            buyNowPrice,
            imageUriList,
            auctionEnd,
            duplication = false,
            winnerId = ""
        )

        // Upload images to Firebase Storage
        val imageUrls = ArrayList<String>(MAX_PHOTOS)
        val uploadCount = imageUriList.size
        var uploadCompleteCount = 0

        for (i in 0 until imageUriList.size) {
            val imageUri = imageUriList[i]
            val imageRef = storageRef.child("auctions/$auctionId/$i.jpg")
            val uploadTask = imageRef.putFile(imageUri.toUri())

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    imageUrls.add(downloadUri.toString())
                }

                uploadCompleteCount++

                if (uploadCompleteCount == uploadCount) {
                    // All images uploaded, add the image URLs to the auction object
                    auction.imageUrls = imageUrls

                    // Add the auction data to Firestore
                    val db = FirebaseFirestore.getInstance()
                    db.collection("auctions")
                        .document(auctionId)
                        .set(auction)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Auction created successfully.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error creating auction: ${e.message}", Toast.LENGTH_SHORT).show()
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
