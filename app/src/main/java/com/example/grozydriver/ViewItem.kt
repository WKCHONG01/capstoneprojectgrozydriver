package com.example.grozydriver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grozydriver.databinding.ViewitemBinding
import com.example.grozydriver.model.ItemsHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var binding: ViewitemBinding
private lateinit var adapterItemsRecycle: AdapterItemsHistoryRecycle
private lateinit var firebaseAuth: FirebaseAuth

class ViewItem: AppCompatActivity() {
    companion object{
        var itemsArraylist: ArrayList<ItemsHistory>? = ArrayList()
        var id: String = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewitemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        loadItemsFromFirebase()
        initializeBinding()
    }

    private fun loadItemsFromFirebase() {
        val ref = FirebaseDatabase.getInstance("https://testing-16c76-default-rtdb.firebaseio.com").getReference("Drivers")
        ref.child(firebaseAuth.uid!!).child("DeliveryHistory").child(id).child("Items").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemsArraylist!!.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ItemsHistory::class.java)
                    if (model != null) {
                        itemsArraylist!!.add(model)
                    }
                }
                val linearLayoutManager = object: LinearLayoutManager(this@ViewItem){
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                adapterItemsRecycle = AdapterItemsHistoryRecycle(this@ViewItem, itemsArraylist!!)
                binding.itemRecycle.layoutManager = linearLayoutManager
                binding.itemRecycle.adapter = adapterItemsRecycle
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }
    private fun initializeBinding(){
        binding.backBtn.setOnClickListener(){
            finish()
        }
    }
}