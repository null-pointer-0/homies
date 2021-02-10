package com.example.homies.Models

data class Msgmodel(var userid:String,
                    var message:String,
                    var timestamp:Long ?= null)
{
    constructor():this(
        "","",null
    )
    constructor(userid:String,
                message:String):this(userid,message,null)
}
