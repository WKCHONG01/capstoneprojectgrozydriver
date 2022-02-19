package com.example.drivergrozy.model

class Orders {
    var address:String? = null
    var orderByUid:String? = null
    var orderCost:Double? = null
    var orderId:String? = null
    var orderStatus:String? = null
    var orderTime:Long? = null
    var phoneNumber:String? = null
    var userName:String? = null

    constructor(){

    }

    constructor(

        address:String?,
        orderByUid:String?,
        orderCost:Double?,
        orderId:String?,
        orderStatus:String?,
        orderTime: Long?,
        phoneNumber:String?,
        userName:String?
    ){

        this.address = address!!
        this.orderByUid = orderByUid
        this.orderCost = orderCost
        this.orderId = orderId
        this.orderStatus = orderStatus
        this.orderTime = orderTime
        this.phoneNumber = phoneNumber
        this.userName = userName
    }
}