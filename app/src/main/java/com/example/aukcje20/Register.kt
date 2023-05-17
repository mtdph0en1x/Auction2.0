package com.example.aukcje20

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*


class Register : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var registerButton: Button
    private lateinit var emailR: EditText
    private lateinit var passwordR: EditText
    private lateinit var VerifyPass: EditText
    private lateinit var NickR: EditText

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //val currentUser = auth.currentUser

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        emailR = findViewById(R.id.email)
        passwordR = findViewById(R.id.password)
        VerifyPass = findViewById(R.id.Verifypassword)
        NickR = findViewById(R.id.nickname)
        registerButton = findViewById(R.id.registerButton)


        registerButton.setOnClickListener {
            checkCredentials()
        }
    }

    private fun checkCredentials() {
        val email = emailR.text.toString()
        val password = passwordR.text.toString()
        val nickname = NickR.text.toString()
        val verify = VerifyPass.text.toString()

        if(email.isEmpty() || !email.contains("@"))
        {
            emailR.error = "Enter correct email"
            //Toast.makeText(this,"Wrong Email",Toast.LENGTH_SHORT).show()
        }
        else if(nickname.length < 6)
        {
            NickR.error = "Too short Nickname (at least 6 letters)"
            //Toast.makeText(this,"Too short Password (at least 6 letters)",Toast.LENGTH_SHORT).show()
        }
        else if(password.length < 6)
        {
            passwordR.error = "Too short Password (at least 6 letters)"
            //Toast.makeText(this,"Too short Password (at least 6 letters)",Toast.LENGTH_SHORT).show()
        }
        else if(password != verify)
        {
            VerifyPass.error = "Incorrect Passwords"
            //Toast.makeText(this,"Incorrect Passwords",Toast.LENGTH_SHORT).show()
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
                    val UserC = User(auth.currentUser?.uid.toString(), nickname.text.toString())
                    db.collection("users")
                        .document(auth.currentUser?.uid.toString())
                        .set(UserC)

                    user?.sendEmailVerification()?.addOnSuccessListener {
                        Toast.makeText(this,"Email verification sent",Toast.LENGTH_SHORT).show()
                    }?.addOnFailureListener()
                    {
                        Toast.makeText(this,"Email verification sent - Failure",Toast.LENGTH_SHORT).show()
                        return@addOnFailureListener
                    }

                    val intent = Intent(this, Login::class.java)
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




