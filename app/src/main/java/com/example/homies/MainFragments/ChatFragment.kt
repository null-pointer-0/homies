package com.example.homies.MainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homies.Adapter.userAdapter
import com.example.homies.Models.User
import com.example.homies.databinding.FragmentChatBinding
import com.example.homies.Async.mythread
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {
    lateinit var binding:FragmentChatBinding
    var uList:ArrayList<User> = ArrayList()
    var fireDb:FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var auth:FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater,container,false)
        var adp = userAdapter(context!!,uList)
        binding.chatView.adapter = adp
        binding.chatView.layoutManager = LinearLayoutManager(context)
        auth = FirebaseAuth.getInstance()
        fireDb.reference.child("users").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                uList.clear()
                var t1 = mythread(snapshot,auth,adp,uList)
                t1.run()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        return binding.root
    }
}