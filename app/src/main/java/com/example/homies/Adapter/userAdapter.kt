package com.example.homies.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.homies.R
import androidx.recyclerview.widget.RecyclerView
import com.example.homies.UI.Chatdetail
import com.example.homies.Models.User
import com.example.homies.Async.mythread2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.userlist.view.*

class userAdapter(var context: Context, var userlist:ArrayList<User>):RecyclerView.Adapter<userAdapter.userviewholder>() {
    class userviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var img: ImageView = itemView.profileimage
            var text1:TextView = itemView.profilename
            var text2:TextView = itemView.lastchat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userviewholder {
        var viewinflt = LayoutInflater.from(context).inflate(R.layout.userlist,parent,false)
        return userviewholder(viewinflt)
    }

    override fun onBindViewHolder(holder: userviewholder, position: Int) {
        var curr = userlist[position]
        if(curr.pfp.isNullOrEmpty())
            holder.img.setImageResource(R.drawable.avatar)
        else
            Picasso.get().load(curr.pfp)
                .into(holder.img)
        holder.text1.text = curr.name
        FirebaseDatabase.getInstance().reference.child("chats")
                .child(FirebaseAuth.getInstance().uid+curr.userid)
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var t2 = mythread2(holder,snapshot)
                        t2.run()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context,"network error",Toast.LENGTH_SHORT).show()
                    }
                })
        holder.itemView.setOnClickListener {
            var intent = Intent(context, Chatdetail::class.java)
            intent.putExtra("userid",curr.userid)
            intent.putExtra("profilepic",curr.pfp)
            intent.putExtra("username",curr.name)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return userlist.size
    }
}