package com.example.homies.Models

data class User(
    var name:String,
    var userid:String,
    var passkey:String,
    var lastmsg:String,
    var mail:String,
    var pfp:String ?= null
){
    constructor():this(
        "","","","","",""
    )
    constructor(
        name:String,
        passkey:String,
        mail:String
    ):this(name,"",passkey,"",mail,"")
}