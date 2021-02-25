package com.example.homies.UI

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homies.Adapter.ChatAdapter
import com.example.homies.Models.Msgmodel
import com.example.homies.Models.User
import com.example.homies.R
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
            startActivity(Intent(this@Chatdetail, MainActivity::class.java))
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
        binding.allmessages.post(Runnable {
            binding.allmessages.scrollToPosition(adptr.getItemCount() - 1)

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
        binding.info.setOnClickListener {
            var intent:Intent = Intent(this,Info::class.java)
            intent.putExtra("uid",receiverid)
            intent.putExtra("uname",username)
            intent.putExtra("pfp",pfp)
            startActivity(intent)
        }
        binding.call.setOnClickListener {
            fireDb.reference.child("users").child(receiverid.toString())
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var u1 = snapshot.getValue(User::class.java)
                        if(u1!!.private == false && u1!!.number != null){
                            var phoneNumber = u1.number!!.toLong()
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phoneNumber")
                            }
                            if(intent.resolveActivity(packageManager) != null)
                                startActivity(intent)
                        }
                        else if(u1!!.private == true){
                            Toast.makeText(this@Chatdetail,"You can't make call to this user",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Chatdetail,error.message,Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}