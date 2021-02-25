package com.example.homies.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.homies.Async.Mythread3
import com.example.homies.Models.User
import com.example.homies.R
import com.example.homies.databinding.ActivityInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class Info : AppCompatActivity() {
    lateinit var binding:ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        var name = intent.getStringExtra("uname")
        var userid = intent.getStringExtra("uid")
        var profileimage = intent.getStringExtra("pfp")
        binding.nameinfo.text = name
        if(profileimage != null) {
            var t3 = Mythread3(userid!!, binding, profileimage, this@Info)
            t3.run()
        }else{
            var t3 = Mythread3(userid!!, binding,"", this@Info)
            t3.run()
        }
        binding.backarrow.setOnClickListener {
            startActivity(Intent(this,Chatdetail::class.java))
        }
    }
}