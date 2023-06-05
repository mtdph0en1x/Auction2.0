package com.example.aukcje20.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.aukcje20.DataClasses.User
import com.example.aukcje20.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class EditProfile : AppCompatActivity() {


    private val db = Firebase.firestore

    private lateinit var profNickname: EditText
    private lateinit var profEmail: EditText
    private lateinit var profPassword: EditText
    private lateinit var profButton: Button
    private lateinit var profBack: ImageButton
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profNickname = findViewById(R.id.et_nickname)
        profEmail = findViewById(R.id.et_email)
        profPassword = findViewById(R.id.et_password)
        profButton = findViewById(R.id.btn_edit_profile)
        profBack = findViewById(R.id.gobackbtn)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid.toString()

        val docUser = db.collection("users").document(userUID)
        auth = FirebaseAuth.getInstance()

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
                R.id.nav_logout ->{
                    auth.signOut()
                    val intent = Intent(this,Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
        supportActionBar?.title = "Edit your profile"


        profButton.setOnClickListener{

            val nicknameUser = profNickname.text.toString()
            val emailUser = profEmail.text.toString()
            val passwordUser = profPassword.text.toString()
            
            if(nicknameUser.isNotEmpty())
            {
                db.collection("nicknames").document(nicknameUser).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            // The document exists in the collection
                            profNickname.error = "Nickname already exists"
                            // Perform your desired operations here
                        } else {
                            db.collection("users").document(userUID).get().addOnSuccessListener { document ->
                                val user: User? = document.toObject(User::class.java)
                                val nick = user?.nickname.toString()

                                val firestore = Firebase.firestore

                                firestore.collection("nicknames").document(nick).get().addOnSuccessListener { doc ->
                                    if (doc.exists()) {
                                        val data = doc.data
                                        if (data != null) {
                                            firestore.collection("nicknames").document(nicknameUser).set(data)
                                                .addOnSuccessListener {
                                                    firestore.collection("nicknames").document(nick).delete()
                                                        .addOnSuccessListener {
                                                            // Nickname swapped successfully
                                                            docUser.update("nickname", nicknameUser)
                                                                .addOnSuccessListener {
                                                                    // Nickname updated in users collection
                                                                }
                                                        }
                                                }
                                        }
                                    } else {
                                        // Nickname does not exist in nicknames collection
                                        docUser.update("nickname", nicknameUser)
                                            .addOnSuccessListener {
                                                // Nickname updated in users collection
                                            }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            if(emailUser.isNotEmpty())
            {
                currentUser?.updateEmail(emailUser)
                docUser.update("email",emailUser)
            }
            if(passwordUser.isNotEmpty())
            {
                currentUser?.updatePassword(passwordUser)
            }

        }

        profBack.setOnClickListener {
            this.finish()
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