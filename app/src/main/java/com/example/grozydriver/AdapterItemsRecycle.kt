package com.example.grozydriver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.drivergrozy.model.Items
import com.example.drivergrozy.model.Recipes
import com.example.grozydriver.R
import com.example.grozydriver.model.ItemsHistory
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterItemsRecycle(private var context: Context,
                          private var itemsList: ArrayList<Items>
): RecyclerView.Adapter<AdapterItemsRecycle.MyViewHolder>() {

    private var id: String? = null

    private fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable){
        val option = RequestOptions().placeholder(progressDrawable).error(R.mipmap.ic_launcher)
        Glide.with(context).setDefaultRequestOptions(option).load(uri).into(this)
    }

    private fun getProgressDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            start()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.items,parent,false)

        return MyViewHolder(itemView)
    }

    private fun loadRecipesImageFromFirebase(holder: MyViewHolder) {

        val ref = FirebaseDatabase.getInstance("https://testing-16c76-default-rtdb.firebaseio.com").getReference("Recipes")
        ref.child(id.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.titleImage.loadImage("${snapshot.child("image").value}", getProgressDrawable(context))

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = itemsList!![position]
        id = currentitem.pId
        holder.parcelId.text = currentitem.pId
        holder.Quantity.text = currentitem.quantity.toString()
        holder.recipeName.text = currentitem.title
        loadRecipesImageFromFirebase(holder)

    }

    override fun getItemCount(): Int {
        return itemsList!!.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val parcelId: TextView = itemView.findViewById(R.id.parcelid)
        val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        val Quantity: TextView = itemView.findViewById(R.id.quantity)
        val titleImage : ShapeableImageView = itemView.findViewById(R.id.recipe_img)
    }


}