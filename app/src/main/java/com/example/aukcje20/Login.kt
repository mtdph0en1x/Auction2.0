
package com.example.aukcje20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var loginButton: Button
    private lateinit var emailL: EditText
    private lateinit var passwordL: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val register = findViewById<TextView>(R.id.noAccTextView)
        register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        emailL = findViewById(R.id.username)
        passwordL = findViewById(R.id.password)
        loginButton = findViewById(R.id.login2)

        loginButton.setOnClickListener {
            val email = emailL.text.toString()
            val password = passwordL.text.toString()

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    if (user != null) {
                        if (user.isEmailVerified()) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,"User's email is not verified",Toast.LENGTH_SHORT).show()
                        }


                    }
                }
            }
    }
}