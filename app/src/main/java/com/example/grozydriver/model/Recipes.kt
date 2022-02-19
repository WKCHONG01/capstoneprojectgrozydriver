package com.example.drivergrozy.model

class Recipes {

    var amount:String? = null
    var categoryID:String? = null
    var description:String? = null
    var id:String? = null
    var title:String? = null
    var image:String? = null
    var ingredients:String? = null
    var instructions:String? = null
    var price:Double = 0.0
    var timestamp:Long = 0
    var uid:String? = null
    var video:String? = null

    constructor(){

    }

    constructor(

        title:String?,
        image:String?,
        amount:String?,
        description:String?,
        categoryID:String?,
        id:String?,
        ingredients:String?,
        price:Double,
        timestamp:Long,
        uid:String?,
        video:String?,
        instructions: String?
    ){

        this.title = title!!
        this.image = image
        this.amount = amount
        this.description = description
        this.categoryID = categoryID
        this.id = id
        this.ingredients = ingredients
        this.price = price
        this.timestamp = timestamp
        this.uid = uid
        this.video = video
        this.instructions = instructions
    }
}