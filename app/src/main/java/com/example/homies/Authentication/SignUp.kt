package com.example.homies.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.homies.UI.MainActivity
import com.example.homies.Models.User
import com.example.homies.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    lateinit var binding:ActivitySignUpBinding
    lateinit var mAuth:FirebaseAuth
    lateinit var fireDB:FirebaseDatabase
    lateinit var mgso:GoogleSignInOptions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        fireDB = FirebaseDatabase.getInstance()

        binding.signupbt.setOnClickListener {
            onCLicked()
        }
        binding.alreadytext.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }
    }
    fun onCLicked(){
        mAuth.createUserWithEmailAndPassword(binding.enteruser.text.toString(),
                                            binding.enterpass.text.toString())
                        .addOnCompleteListener {
                            var name = binding.editname.text.toString()
                            var email = binding.enteruser.text.toString()
                            var pass = binding.enterpass.text.toString()
                            if(it.isSuccessful){
                                var user = User(name,pass,email)
                                var userid = it.result!!.user!!.uid
                                fireDB.reference.child("users").child(userid).setValue(user)
                                startActivity(Intent(this, MainActivity::class.java))
                                Toast.makeText(this,"signup successful",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
                            }
                        }
    }
}