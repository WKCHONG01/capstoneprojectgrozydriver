package com.example.grozydriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.grozydriver.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Drivers")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val icNum = "${snapshot.child("icNum").value}"

                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                    binding.emailTv.text = email
                    binding.phoneTv.text = phoneNumber
                    binding.memberDateTv.text = formattedDate
                    binding.iCNumTv.text = icNum

                    try{
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(binding.profileIv)
                    }catch(e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}