package com.example.grozydriver

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.grozydriver.model.History

class AdapterHistoryAddressRecycle(
    private var context: Context,
    private var HistoryList: ArrayList<History>?
): RecyclerView.Adapter<AdapterHistoryAddressRecycle.MyViewHolder>(){

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var locationaddress: TextView = itemView.findViewById(R.id.location_address_hist)
        var parcelId: TextView = itemView.findViewById(R.id.parcelid_hist)
        var owner: TextView = itemView.findViewById(R.id.owner_name_hist)
        var phnumber: TextView = itemView.findViewById(R.id.phoneNumber_hist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val historyView = LayoutInflater.from(context).inflate(R.layout.addresses,parent,false)
        return MyViewHolder(historyView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.locationaddress.text = HistoryList!![position].address.toString()
        holder.parcelId.text = HistoryList!![position].orderId.toString()
        holder.owner.text = HistoryList!![position].userName.toString()
        holder.phnumber.text = HistoryList!![position].phoneNumber.toString()
        var id = HistoryList!![position].orderId!!.toString()
        holder.itemView.rootView.setOnClickListener(){
            ViewItem.id = id
            val mIntent = Intent(context, ViewItem()::class.java)
            context.startActivity(mIntent)
        }
    }

    override fun getItemCount(): Int {
        return HistoryList!!.size
    }


}