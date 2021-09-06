package com.example.guruapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var btn1:Button
    lateinit var btn2:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1=findViewById(R.id.btn1)
        btn2=findViewById(R.id.btn2)

        btn1.setOnClickListener {
            val intent=Intent(this, Login::class.java)
            startActivity(intent)
        }

        btn2.setOnClickListener {
            val intent=Intent(this, mainpage::class.java)
            startActivity(intent)
        }
    }
}