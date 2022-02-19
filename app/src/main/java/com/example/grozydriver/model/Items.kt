package com.example.drivergrozy.model

class Items {
    var pId:String? = null
    var price:String? = null
    var productDescription:String? = null
    var quantity:Int? = null
    var title:String? = null

    constructor(){

    }

    constructor(

        pId:String?,
        price:String?,
        productDescription:String?,
        quantity:Int?,
        title:String?
    ){

        this.pId = pId!!
        this.price = price
        this.productDescription = productDescription
        this.title = title
        this.quantity = quantity
    }
}