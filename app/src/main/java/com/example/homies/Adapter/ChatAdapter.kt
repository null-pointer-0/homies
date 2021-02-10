package com.example.homies.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homies.Models.Msgmodel
import com.example.homies.R
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(var context: Context, var msgs:ArrayList<Msgmodel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var senderviewtype:Int = 1
    var recieverviewtype:Int = 2
    class recieverviewholder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var rcvrmsg: TextView = itemView.findViewById(R.id.receivedmsg)
        var rcvrtime:TextView = itemView.findViewById(R.id.time)
    }
    class senderviewholder(itemView: View):RecyclerView.ViewHolder(itemView){
        var sndrrmsg: TextView = itemView.findViewById(R.id.sendmsg)
        var sndrtime:TextView = itemView.findViewById(R.id.stime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == senderviewtype)
            senderviewholder(LayoutInflater.from(context).inflate(R.layout.chatsendlayout,parent,false))
        else
            recieverviewholder(LayoutInflater.from(context).inflate(R.layout.chatreceivelayout,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var msgmodel:Msgmodel = msgs[position]
        if(holder.javaClass.equals(senderviewholder::class.java)){
            (holder as senderviewholder).sndrrmsg.text = msgmodel.message
        }
        else
            (holder as recieverviewholder).rcvrmsg.text = msgmodel.message
    }

    override fun getItemCount(): Int {
        return msgs.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(msgs[position].userid == FirebaseAuth.getInstance().uid)
            senderviewtype
        else recieverviewtype
    }
}