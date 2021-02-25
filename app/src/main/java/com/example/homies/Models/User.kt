package com.example.homies.Models

import android.text.Editable

data class User(
    var name:String,
    var userid:String,
    var passkey:String,
    var lastmsg:String,
    var mail:String,
    var status:String,
    var private:Boolean ?= true,
    var number:Long ?= null,
    var pfp:String ?= null
) {
    constructor() : this(
        "", "", "", "", "", ""
    )

    constructor(
        name: String,
        passkey: String,
        mail: String
    ) : this(name, "", passkey, "", mail, "")
}