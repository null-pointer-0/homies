package com.example.homies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.homies.Adapter.FragmentAdapter
import com.example.homies.Authentication.SignIn
import com.example.homies.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.contentview.adapter = FragmentAdapter(supportFragmentManager)
        binding.tabview.setupWithViewPager(binding.contentview)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflt = menuInflater
        inflt.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout->{auth.signOut()
                            startActivity(Intent(this@MainActivity, SignIn::class.java))
                            finish()}
        }
        return true
    }
}