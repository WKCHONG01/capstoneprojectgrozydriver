package com.example.grozydriver.model

class ItemsHistory {

    var pId:String? = null
    var quantity:Int? = null
    var title:String? = null

    constructor(){

    }

    constructor(

        pId:String?,
        quantity:Int?,
        title:String?
    ){

        this.pId = pId!!
        this.title = title
        this.quantity = quantity
    }
}