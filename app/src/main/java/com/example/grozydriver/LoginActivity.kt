package com.example.grozydriver

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.grozydriver.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try{
            actionBar = supportActionBar!!
            actionBar.title = "Login"
        }catch(ignored : NullPointerException){
        }
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCanceledOnTouchOutside(false)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        /*binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, signUpActivity::class.java))
        }*/

        binding.loginBtn.setOnClickListener {
            validateData()
        }

    }

    private fun validateData(){
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Invalid email format")
        }
        else if (TextUtils.isEmpty(password)){
            //no password
            binding.passwordEt.error ="Please enter password"
        }
        else{
            firebaseLogin(email)
        }
    }

    private fun firebaseLogin(email : String){
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"LoggedIn as $email", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.putExtra("email",email)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            intent.putExtra("email",email)
            startActivity(intent)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun readData(email : String){
        database = FirebaseDatabase.getInstance().getReference("Drivers")
        database.child(email).get().addOnSuccessListener {

            if (it.exists()){
                val phoneNoFromDB = it.child("phoneNumber").value
                val email = it.child("email").value
            } else {
                Toast.makeText(this, "User Doesn't Exist", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener {
            }

    }
}