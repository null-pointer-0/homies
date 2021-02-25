package com.example.homies.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.homies.UI.MainActivity
import com.example.homies.Models.User
import com.example.homies.R
import com.example.homies.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {
    lateinit var binding:ActivitySignInBinding
    lateinit var mauth:FirebaseAuth
    lateinit var mgsc:GoogleSignInClient
    var RC_SIGN_IN:Int = 29
    lateinit var fireDb:FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fireDb = FirebaseDatabase.getInstance()
        mauth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mgsc = GoogleSignIn.getClient(this,gso)
        binding.signinbt.setOnClickListener {
            mauth.signInWithEmailAndPassword(binding.enteruser.text.toString()
                ,binding.enterpass.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        binding.enterpass.text.clear()
                    }
                    else
                        Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
                }
        }
        binding.alreadytext.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
        binding.googleaccountbt.setOnClickListener {
            signIn()
        }
        if(mauth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    private fun signIn() {
        val signInIntent = mgsc.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {

            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mauth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var user = mauth.currentUser
                    var user2 = User()
                    user2.userid = user!!.uid
                    user2.name = user!!.displayName.toString()
                    user2.pfp = user!!.photoUrl.toString()
                    fireDb.reference.child("users").child(user.uid).setValue(user2)
                } else {
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }
}