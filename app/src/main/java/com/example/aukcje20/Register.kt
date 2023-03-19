package com.example.aukcje20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Register : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    private lateinit var registerButton: Button
    private lateinit var emailR: EditText
    private lateinit var passwordR: EditText
    private lateinit var VerifyPassword: EditText

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        emailR = findViewById(R.id.email)
        passwordR = findViewById(R.id.password)
        VerifyPassword = findViewById(R.id.Verifypassword)
        registerButton = findViewById(R.id.registerButton)


        registerButton.setOnClickListener {
            checkCredentials()
        }
    }

    private fun checkCredentials() {
        val email = emailR.text.toString()
        val password = passwordR.text.toString()
        val verify = VerifyPassword.text.toString()

        if(email.isEmpty() || !email.contains("@"))
        {
            Toast.makeText(this,"Wrong Email",Toast.LENGTH_SHORT).show()
        }
        else if(password.length < 6)
        {
            Toast.makeText(this,"Too short Password (at least 6 letters)",Toast.LENGTH_SHORT).show()
        }
        else if(password != verify)
        {
            Toast.makeText(this,"Incorrect Passwords",Toast.LENGTH_SHORT).show()
        }
        else
        {
            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }
}




