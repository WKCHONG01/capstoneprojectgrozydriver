package com.example.grozydriver

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.grozydriver.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class signUpActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivitySignUpBinding

    //ActionBar
    private lateinit var actionBar : ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    private var email = ""
    private var password = ""
    private var phonenumber = ""
    private var iCNum = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try{
            actionBar = supportActionBar!!
            actionBar.title = "Sign Up"
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }catch(ignored : NullPointerException){

        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating account In...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData(){
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        phonenumber = binding.phoneNumEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()
        iCNum = binding.icNumTil.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Invalid email format"
        }
        else if (TextUtils.isEmpty(password)) {
            binding.passwordEt.error = "Please enter password"
        }
        else if (TextUtils.isEmpty(iCNum)) {
            binding.icNumTil.error = "Please enter Identification Number"
        }
        else if (password.length < 8){
            binding.passwordEt.error = "Password must at least 8 characters long"
        }
        else if (!password.matches(".*[0-9].*".toRegex())){
            binding.passwordEt.error = "Password must at least one number"
        }
        else if (!password.matches(".*[a-z].*".toRegex())){
            binding.passwordEt.error = "Password must at least a lower case character"
        }
        else if (!password.matches(".*[A-Z].*".toRegex())){
            binding.passwordEt.error = "Password must at least a upper case character"
        }
        else if (!password.matches(".*[@#\$%^&+=*()-_=+~`':;?/>.<,].*".toRegex())){
            binding.passwordEt.error = "Password must at least a special character"
        }
        else if (!password.matches(".*\\S+\$.*".toRegex())){
            binding.passwordEt.error = "Password can't have white space"
        }
        else if (!phonenumber.matches("^(01)[0-46-9]*[0-9]{7,8}\$".toRegex())){
            binding.phoneNumEt.error = "Invalid phone number"
        }
        else if (cPassword.isEmpty()){
            binding.cPasswordEt.error = "Confirm password"
        }
        else if (password != cPassword){
            binding.cPasswordEt.error = "Password doesn't match"
        }
        else{
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp(){
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Account created with email $email", Toast.LENGTH_SHORT).show()
                updateUserInfo()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"SignUp Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo(){
        progressDialog.setMessage("Saving user info...")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["profileImage"] = ""
        hashMap["phoneNumber"] = phonenumber
        hashMap["icNum"] = iCNum
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Drivers")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Account created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}