package com.example.grozydriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.grozydriver.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var actionBar : ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
        startActivity(Intent(this, NavigationActivity::class.java))
        binding.orderNavigationBtn.setOnClickListener(){
            startActivity(Intent(this, NavigationActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.historyListBtn.setOnClickListener{
            startActivity(Intent(this,HistoryActivity::class.java))
        }

    }

    private fun checkUser(){
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user not null, user is logged in, get user info
            val email = firebaseUser.email
            //set to text view
            binding.profileBtn.visibility = View.VISIBLE
        }
        else{
            //user is null, user is not logged in
            binding.profileBtn.visibility = View.GONE
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}