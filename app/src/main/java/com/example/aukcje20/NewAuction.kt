@file:Suppress("DEPRECATION")

package com.example.aukcje20


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aukcje20.databinding.ActivityNewAuctionBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.text.SimpleDateFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class NewAuction : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth




    private lateinit var binding: ActivityNewAuctionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAuctionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get a reference to the Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // get references to the views
        val nameEditText = binding.editTextName
        val descriptionEditText = binding.editTextDescription
        val startPriceEditText = binding.editTextStartPrice
        val buyNowPriceEditText = binding.editTextBuyNowPrice
        val addPhotoButton = binding.buttonAddPhoto
        val createAuctionButton = binding.buttonCreateAuction
        val addDateButton = binding.buttonAddDate
        val photoText = binding.tvAddPhoto

        // set up click listeners for the buttons
        addPhotoButton.setOnClickListener { openFileChooser() }

        val myCalendar = Calendar.getInstance()


        val datePicker = DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timePicker = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    myCalendar.set(Calendar.MINUTE, minute)
                    myCalendar.set(Calendar.SECOND, 0) // Set seconds to 0 or change as needed

                    val myFormat = "dd-MM-yyyy HH:mm:ss" // Format pattern including date and time
                    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                    photoText.text = sdf.format(myCalendar.time)
                },
                0,
                0,
                true
            )
            timePicker.show()
        }


        addDateButton.setOnClickListener{
            DatePickerDialog(this,datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH),)
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
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun createAuction(auctionid: String, uid: String, name: String, description: String, startPrice: Double?, buyNowPrice: Double?,dateAuction: String) {
        if (imageUri == null) {
            Toast.makeText(this, "Please add a photo", Toast.LENGTH_SHORT).show()
            return
        }

        // create a reference to the Firebase Storage instance
        val storage = Firebase.storage
        val storageRef = storage.reference


        // create a reference to the image file and upload it to Firebase Storage
        val imageRef = storageRef.child("images/${UUID.randomUUID()}")

        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val date = dateFormat.parse(dateAuction)
        val timestamp = date.time

        imageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                // get the download URL for the image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // create a new auction object

                    val auction = auth.currentUser?.let {
                        Auction(
                            auctionid = auctionid,
                            uid = uid,
                            name = name,
                            description = description,
                            startPrice = startPrice ?: 0.0,
                            buyNowPrice = buyNowPrice ?: 0.0,
                            imageUrl = uri.toString(),
                            auctionEnd = dateAuction,
                            bidders = emptyList()
                        )
                    }

                    // add the new auction to the Firebase Firestore database
                    val db = Firebase.firestore

                    if (auction != null) {
                        db.collection("auctions").document(auctionid)
                            .set(auction)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Auction created successfully", Toast.LENGTH_SHORT).show()
                                finish()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error creating auction: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Picasso.get().load(imageUri).into(binding.imageViewPhoto)
        }
    }
}
