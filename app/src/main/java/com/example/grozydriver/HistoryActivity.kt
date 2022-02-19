package com.example.grozydriver

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grozydriver.databinding.ActivityHistoryBinding
import com.example.grozydriver.model.History
import com.example.grozydriver.model.HistoryAddressItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryActivity: AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityHistoryBinding
    private var historydateFirebase: ArrayList<String>? = ArrayList()
    private lateinit var adapterHistoryRecycle: AdapterHistoryRecycle
    private var addressArrayList: ArrayList<History> = ArrayList()
    private var historyaddressFirebase: ArrayList<HistoryAddressItems>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        loadHistoryFromFirebase()
        binding.backBtn.setOnClickListener(){
            finish()
        }
    }
    private fun sortTime(arrayList: ArrayList<HistoryAddressItems>?): ArrayList<HistoryAddressItems>{
        var index = arrayList!!.size - 1
        var sortedArraylist: ArrayList<HistoryAddressItems> = ArrayList()
        while(index >= 0){
            sortedArraylist.add(historyaddressFirebase!![index])
            index -= 1
        }
        historyaddressFirebase!!.clear()
        return sortedArraylist
    }

    private fun sortDate(arrayList: ArrayList<String>?): ArrayList<String>{
        var index = arrayList!!.size - 1
        var sortedArraylist: ArrayList<String> = ArrayList()
        while(index >= 0){
            sortedArraylist.add(historydateFirebase!![index])
            index -= 1
        }
        historydateFirebase!!.clear()
        return sortedArraylist
    }

    private fun loadHistoryFromFirebase() {

        val ref = FirebaseDatabase.getInstance("https://testing-16c76-default-rtdb.firebaseio.com")
            .getReference("Drivers")
        ref.child(firebaseAuth.uid!!).child("DeliveryHistory").orderByChild("deliverTime")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (historydateFirebase!!.isNotEmpty()) {
                        historydateFirebase!!.clear()
                    }
                    var currentTime: String = ""
                    for (ds in snapshot.children) {
                        val modelImage = ds.getValue(History::class.java)
                        if (modelImage != null) {
                            if(modelImage.deliverTime!! != currentTime.toString()) {
                                historydateFirebase!!.add(modelImage.deliverTime!!)
                                currentTime = modelImage.deliverTime!!
                            }
                        }
                    }
                    ref.child(firebaseAuth.uid!!).child("DeliveryHistory").orderByChild("deliverTime")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var index = 0
                                if (historyaddressFirebase!!.isNotEmpty()) {
                                    historyaddressFirebase!!.clear()
                                }
                                if(snapshot.exists()) {
                                    for (ds in snapshot.children) {

                                        val modelImage = ds.getValue(History::class.java)

                                        if (modelImage != null) {
                                            if (modelImage.deliverTime!! != historydateFirebase!![index].toString()) {
                                                index += 1
                                                historyaddressFirebase!!.add(HistoryAddressItems(
                                                    historydateFirebase!![index],
                                                    modelImage))


                                            } else {
                                                historyaddressFirebase!!.add(HistoryAddressItems(
                                                    historydateFirebase!![index],
                                                    modelImage))
                                            }

                                        }
                                    }
                                    historyaddressFirebase = sortTime(historyaddressFirebase)
                                    historydateFirebase = sortDate(historydateFirebase)
                                }
                                val linearLayoutManager = object: LinearLayoutManager(this@HistoryActivity) {
                                        override fun canScrollVertically(): Boolean {
                                            return false
                                        }

                                        override fun canScrollHorizontally(): Boolean {
                                            return false
                                        }
                                    }
                                adapterHistoryRecycle = AdapterHistoryRecycle(this@HistoryActivity,
                                    historydateFirebase,historyaddressFirebase)
                                binding.historyRecycle.layoutManager = linearLayoutManager
                                binding.historyRecycle.adapter = adapterHistoryRecycle
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })



                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
    }

}
