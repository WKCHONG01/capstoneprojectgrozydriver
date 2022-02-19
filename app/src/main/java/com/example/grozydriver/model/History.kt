package com.example.grozydriver.model

class History {
    var address:String? = null
    var orderByUid:String? = null
    var orderId:String? = null
    var orderStatus:String? = null
    var deliverTime:String? = null
    var phoneNumber:String? = null
    var userName:String? = null

    constructor(){

    }

    constructor(

        address:String?,
        orderByUid:String?,
        orderId:String?,
        orderStatus:String?,
        deliverTime: String?,
        phoneNumber:String?,
        userName:String?
    ){

        this.address = address!!
        this.orderByUid = orderByUid
        this.orderId = orderId
        this.orderStatus = orderStatus
        this.deliverTime = deliverTime
        this.phoneNumber = phoneNumber
        this.userName = userName
    }
}