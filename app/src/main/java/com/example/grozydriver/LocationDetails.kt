package com.example.grozydriver

import android.location.Address
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivergrozy.model.Items
import com.example.drivergrozy.model.Orders
import com.example.grozydriver.databinding.ActivityNavigationBinding
import com.example.grozydriver.databinding.FinishConfirmationBinding
import com.example.grozydriver.databinding.LocationdetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LocationDetails: AppCompatActivity() {
    private lateinit var binding: LocationdetailsBinding
    private lateinit var adapterLocationRecycle: AdapterLocationRecycle
    private var currentitem: Orders = Orders()
    private lateinit var adapterItemsRecycle: AdapterItemsRecycle
    private lateinit var firebaseAuth: FirebaseAuth
    companion object{
        var position: Int = 0
        var index: Int = 0
        var id: String? = ""
        var recipeArrayList: ArrayList<Orders>? = ArrayList()
        var locationList: ArrayList<String>? = ArrayList()
        var addressList: ArrayList<Address>? = ArrayList()
        var itemsArraylist: ArrayList<Items>? = ArrayList()
        var addressID: ArrayList<String>? = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        var i = 0
        while (i < recipeArrayList!!.size){
            if(recipeArrayList!![i].orderId.equals(id)){
                index= i
            }
            i += 1
        }
        loadItemsFromFirebase()
        binding.backBtn.setOnClickListener{
            finish()
        }
        initializeBinding()
    }

    private fun loadItemsFromFirebase() {
        val ref = FirebaseDatabase.getInstance("https://testing-16c76-default-rtdb.firebaseio.com").getReference("Orders")
        ref.child(id.toString()).child("Items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemsArraylist!!.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(Items::class.java)
                    if (model != null) {
                        itemsArraylist!!.add(model)
                    }
                    }
                val linearLayoutManager = object: LinearLayoutManager(this@LocationDetails){
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                adapterItemsRecycle = AdapterItemsRecycle(this@LocationDetails, itemsArraylist!!)
                binding.itemRecycle.layoutManager = linearLayoutManager
                binding.itemRecycle.adapter = adapterItemsRecycle
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }
    private fun initializeBinding(){
        binding.finishBtn.setOnClickListener(){
            val customDialog = LayoutInflater.from(this).inflate(R.layout.finish_confirmation, binding.root,false)
            val bindingRDS = FinishConfirmationBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog).create()
            dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialog.show()
            dialog.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            bindingRDS.confirmConfirmation.setOnClickListener(){
                val bindingMA: ActivityNavigationBinding = ActivityNavigationBinding.inflate(layoutInflater)
                dialog.dismiss()
                finish()

                adapterLocationRecycle = AdapterLocationRecycle(this,
                    locationList,addressList, addressID, recipeArrayList)
                saveHistory()
                modifyOrders()
                deleteItem(position, index)


                /*
                val linearLayoutManager = object: LinearLayoutManager(this@LocationDetails){
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                bindingMA.locationRecycle.layoutManager = linearLayoutManager
                bindingMA.locationRecycle.adapter = adapterLocationRecycle

                 */

            }
            bindingRDS.cancelConfirmation.setOnClickListener(){
                dialog.dismiss()
            }

        }
    }
    fun deleteItem(position: Int, index: Int){

            recipeArrayList!!.removeAt(index)
            locationList!!.removeAt(position)
            addressList!!.removeAt(position)
            addressID!!.removeAt(position)
            NavigationActivity.addressFirebase = locationList
        NavigationActivity.addressList = addressList
        NavigationActivity.recipeArrayList = recipeArrayList as ArrayList<Orders>
            adapterLocationRecycle.setItems(recipeArrayList,locationList, addressList, addressID)
        NavigationActivity.datasetchged = true


    }
    private fun modifyOrders() {
        val timestamp = System.currentTimeMillis();
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["orderStatus"] = "Delivered" //In Progress/Completed/Cancelled

        val ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.child(id.toString())
            .updateChildren(hashMap)
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to save Delivery History due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun formatTimeStamp(timestamp: Long): String{
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp
        return DateFormat.format("dd/MM/yyyy",cal).toString()
    }
    private fun saveHistory() {

        val timestamp = System.currentTimeMillis();
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["orderId"] = id.toString()
        hashMap["deliverTime"] = formatTimeStamp(timestamp)
        hashMap["orderStatus"] = "Delivered" //In Progress/Completed/Cancelled
        hashMap["orderByUid"] = recipeArrayList!![index].orderByUid.toString()
        hashMap["address"] = recipeArrayList!![index].address.toString()
        hashMap["userName"] = recipeArrayList!![index].userName.toString()
        hashMap["phoneNumber"] = recipeArrayList!![index].phoneNumber.toString()

        val ref = FirebaseDatabase.getInstance().getReference("Drivers")
        ref.child(firebaseAuth.uid!!).child("DeliveryHistory").child(id.toString())
            .setValue(hashMap)
            .addOnSuccessListener {
                for (i in 0 until itemsArraylist!!.size) {
                    val pId: String = itemsArraylist!![i].pId.toString()
                    val title: String = itemsArraylist!![i].title.toString()
                    val quantity: Int = itemsArraylist!![i].quantity!!.toInt()
                    val hashMap1: HashMap<String, Any> = HashMap()
                    hashMap1["pId"] = pId
                    hashMap1["title"] = title
                    hashMap1["quantity"] = quantity
                    ref.child(firebaseAuth.uid!!).child("DeliveryHistory").child(id.toString()).child("Items").child(pId).setValue(hashMap1)
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to save Delivery History due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}