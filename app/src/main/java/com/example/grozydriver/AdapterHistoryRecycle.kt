package com.example.grozydriver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grozydriver.model.History
import com.example.grozydriver.model.HistoryAddressItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterHistoryRecycle(
    private var context: Context,
    private var time: ArrayList<String>?,
    private var HistoryList: ArrayList<HistoryAddressItems>?): RecyclerView.Adapter<AdapterHistoryRecycle.MyViewHolder>() {
    private lateinit var adapterHistoryAddressRecycle: AdapterHistoryAddressRecycle
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var date: TextView? = null
        var addressRecycle: RecyclerView? = null

        init{
            date = itemView.findViewById(R.id.date)
            addressRecycle = itemView.findViewById(R.id.address_recycle_hist)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val historyView = LayoutInflater.from(context).inflate(R.layout.history_address,parent,false)

        return MyViewHolder(historyView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.date!!.text = time!![position]
        var addressArrayList: ArrayList<History> = ArrayList()
        for (i in 0 until HistoryList!!.size){
            if(time!![position] == HistoryList!![i].items.deliverTime){
                addressArrayList.add(HistoryList!![i].items)
            }
        }


        setItemRecycler(holder.addressRecycle,addressArrayList)

    }

    private fun setItemRecycler(recyclerView: RecyclerView?, item: ArrayList<History>?){
        adapterHistoryAddressRecycle = AdapterHistoryAddressRecycle(context,
            item)
        val linearLayoutManager = object: LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView.adapter = adapterHistoryAddressRecycle
    }



    override fun getItemCount(): Int {
        return time!!.size
    }

}