package com.example.homies.UI

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.homies.Models.User
import com.example.homies.R
import com.example.homies.databinding.ActivitySettingsBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.alertlayout.view.*

class Settings : AppCompatActivity() {
    lateinit var binding:ActivitySettingsBinding
    lateinit var firestore: FirebaseStorage
    lateinit var firedb:FirebaseDatabase
    lateinit var fireauth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        fireauth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()
        firestore = FirebaseStorage.getInstance()
        binding.onback.setOnClickListener {
            startActivity(Intent(this@Settings, MainActivity::class.java))
        }
        firedb.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var u1 = snapshot.getValue(User::class.java)
                    if(!u1!!.pfp.isNullOrEmpty())
                        Picasso.get().load(u1!!.pfp).into(binding.profilepic)
                    else
                        binding.profilepic.setImageResource(R.drawable.avatar)

                    binding.statusabout.setText(u1.status.toString())
                    binding.editUname.setText(u1.name.toString())
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Settings,error.message,Toast.LENGTH_SHORT).show()
                }
            })
        binding.savebt.setOnClickListener {
            var status = binding.statusabout.text.toString()
            var uname = binding.editUname.text.toString()
            var map:HashMap<String,Any> = HashMap()
            map["name"] = uname
            map["status"] = status
            firedb.reference.child("users")
                .child(FirebaseAuth.getInstance().uid!!).updateChildren(map)
        }
        binding.addpic.setOnClickListener {
            var intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,12)
        }
        binding.textView4.setOnClickListener {
            startActivity(Intent(this,Phone::class.java))
        }
        binding.textView2.setOnClickListener {
            var udelete:FirebaseUser? = fireauth.currentUser
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialoglayout: View = inflater.inflate(R.layout.alertlayout,null)
            builder.setTitle("Confirmation")
            builder.setView(R.layout.alertlayout)
            builder.setPositiveButton("Done",object:DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    onDoneClicked(dialoglayout, udelete!!)
                }
            })
            builder.setNegativeButton("Cancel",null)
            var dialog = builder.create()
            dialog.show()
        }
    }
    fun onDoneClicked(dialoglayout:View,udelete:FirebaseUser){
        var cere:AuthCredential = EmailAuthProvider.getCredential(dialoglayout.username.text.toString()
            ,dialoglayout.password.text.toString())
        udelete!!.reauthenticate(cere)
            .addOnCompleteListener {
                udelete.delete()
                    .addOnCompleteListener {
                        Toast.makeText(this@Settings, "user deleted", Toast.LENGTH_SHORT).show()
                    }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!!.data != null){
            var file = data.data
            binding.profilepic.setImageURI(file)
            var storeref: StorageReference = firestore.reference.child("pfp")
                .child(FirebaseAuth.getInstance().uid!!)
            storeref.putFile(file!!).addOnSuccessListener {
                storeref.downloadUrl.addOnCompleteListener(){
                    firedb.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
                        .child("pfp").setValue(it.result.toString())
                }
                Toast.makeText(this,"Image added",Toast.LENGTH_SHORT).show()
            }
        }
    }
}