package com.example.homies

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homies.Adapter.ChatAdapter
import com.example.homies.Models.Msgmodel
import com.example.homies.databinding.ActivityChatdetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class Chatdetail : AppCompatActivity() {
    lateinit var binding:ActivityChatdetailBinding
    var fireDb = FirebaseDatabase.getInstance()
    lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        var senderid = mAuth.uid
        var receiverid = intent.getStringExtra("userid")
        var username = intent.getStringExtra("username")
        var pfp = intent.getStringExtra("profilepic")
        binding.chatname.text = username
        var msgs:ArrayList<Msgmodel> = ArrayList()
        var adptr = ChatAdapter(this,msgs)
        if(pfp.isNullOrEmpty())
            binding.profileimage.setImageResource(R.drawable.avatar)
        else
            Picasso.get().load(pfp).into(binding.profileimage)
        binding.onback.setOnClickListener {
            startActivity(Intent(this@Chatdetail,MainActivity::class.java))
        }
        var sender_room = senderid+receiverid
        var reciever_room = receiverid+senderid
        fireDb.reference.child("chats")
            .child(sender_room).addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    msgs.clear()
                    for(ds:DataSnapshot in snapshot.children){
                        var model:Msgmodel = ds.getValue(Msgmodel::class.java)!!
                        msgs.add(model)
                    }
                    adptr.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        binding.writing.setOnClickListener {
            binding.writing.requestFocus()
            binding.writing.isFocusableInTouchMode = true
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.writing, InputMethodManager.SHOW_FORCED)
        }
        binding.allmessages.adapter = adptr
        binding.allmessages.layoutManager = LinearLayoutManager(this)
        binding.sendmsg.setOnClickListener {
            var msgtosnd = binding.writing.text.toString()
            var model = Msgmodel(senderid!!,msgtosnd)
            model.timestamp = Date().time
            binding.writing.text.clear()
            fireDb.reference.child("chats").child(sender_room)
                .push().setValue(model).addOnCompleteListener {
                    if(it.isSuccessful){
                        fireDb.reference.child("chats").child(reciever_room)
                            .push().setValue(model)
                    }
                }
        }
    }

}