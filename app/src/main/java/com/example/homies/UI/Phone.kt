package com.example.homies.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.homies.Models.User
import com.example.homies.R
import com.example.homies.databinding.ActivityPhoneBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class Phone : AppCompatActivity() {
    lateinit var binding:ActivityPhoneBinding
    lateinit var firedb: FirebaseDatabase
    lateinit var fireauth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        firedb = FirebaseDatabase.getInstance()
        fireauth = FirebaseAuth.getInstance()
        binding.savebut.setOnClickListener {
            onsaveclicked()
        }
        binding.backarrow.setOnClickListener {
            startActivity(Intent(this,Settings::class.java))
        }
        firedb.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var u1 = snapshot.getValue(User::class.java)
                    if(!u1!!.number.toString().isNullOrEmpty()){
                        binding.textView8.text = u1.number.toString()
                        binding.update.setText(u1.number.toString())
                    }
                    if(u1!!.private == true){
                        binding.privateornot.isChecked=true
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Phone,error.message,Toast.LENGTH_SHORT).show()
                }
            })
    }
    fun onsaveclicked(){
        var num = binding.update.text.toString().toLong()
        var priv = false
        if(binding.privateornot.isChecked) priv = true
        var useruid = fireauth.uid.toString()
        if(num == null || num.toString().length < 10){
            Toast.makeText(this,"number not valid",Toast.LENGTH_SHORT).show()
        }else {
            firedb.reference.child("users").child(useruid)
                .child("number").setValue(num)
            binding.textView8.text = num.toString()
        }
        if(priv){
            firedb.reference.child("users").child(useruid)
                .child("private").setValue(true)
        }else{
            firedb.reference.child("users").child(useruid)
                .child("private").setValue(false)
        }
        Toast.makeText(this@Phone,"data updated",Toast.LENGTH_SHORT).show()
    }
}