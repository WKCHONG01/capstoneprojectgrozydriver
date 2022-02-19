package com.example.grozydriver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.drivergrozy.model.Orders
import com.example.grozydriver.R
import com.google.android.material.imageview.ShapeableImageView

class AdapterLocationRecycle(private var context: Context,
                             private var locationlist:ArrayList<String>?,
                             private var addresslist:ArrayList<Address>?,
                             private var addressID:ArrayList<String>?,
                             private var recipelist:ArrayList<Orders>?): RecyclerView.Adapter<AdapterLocationRecycle.MyViewHolder>() {
    private var addresslist2 = addresslist
    private var locationlist2 = locationlist
    private var recipelist2 = recipelist
    private var addressID2 = addressID
    companion object{
        var position2: Int = 0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.location,parent,false)

        return MyViewHolder(itemView)
    }




    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        position2 = position
        var i = 0
        var index = 0
        while (i < recipelist2!!.size){
            if(recipelist2!![i].orderId.equals(addressID2!![position2])){
                index = i
            }
            i += 1
        }
        val currentitem = recipelist2!![index]
        val address: Address = addresslist2!![position]
        val title: String = locationlist2!![position]
        val number: String = currentitem.phoneNumber.toString().trim()
        val id:String? = currentitem.orderId
        holder.address.text = title
        holder.parcelID.text = currentitem.orderId
        holder.ownerName.text = currentitem.userName
        holder.phoneNumber.text = currentitem.phoneNumber
        holder.navigationBtn.setOnClickListener {
            val mIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=${address.latitude},${address.longitude}&mode=d"))
            mIntent.setPackage("com.google.android.apps.maps")
            if (mIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mIntent)
            }

        }
        holder.callBtn.setOnClickListener(){
            val mIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ Uri.encode(number)))
            context.startActivity(mIntent)
        }
        holder.itemView.rootView.setOnClickListener(){
            LocationDetails.addressList = addresslist2
            LocationDetails.locationList = locationlist2
            LocationDetails.recipeArrayList = recipelist2
            LocationDetails.addressID = addressID2
            LocationDetails.id = id
            LocationDetails.index = index
            val mIntent = Intent(context, LocationDetails()::class.java)

            context.startActivity(mIntent)
        }


    }

    override fun getItemCount(): Int {
        return recipelist2!!.size
    }

    fun setItems(recipelistchged: ArrayList<Orders>?, locationlistchged: ArrayList<String>?, addresslistchged: ArrayList<Address>?, addressIDchged: ArrayList<String>?){

        recipelist2 = recipelistchged
        locationlist2 = locationlistchged
        addresslist2 = addresslistchged
        addressID2 = addressIDchged

    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val address: TextView = itemView.findViewById(R.id.location_address)
        val navigationBtn: ImageView = itemView.findViewById(R.id.navigation_btn)
        val callBtn: ImageView = itemView.findViewById(R.id.call_btn)
        val parcelID: TextView = itemView.findViewById(R.id.parcelid)
        val ownerName: TextView = itemView.findViewById(R.id.owner_name)
        val phoneNumber: TextView = itemView.findViewById(R.id.phoneNumber)

    }
}