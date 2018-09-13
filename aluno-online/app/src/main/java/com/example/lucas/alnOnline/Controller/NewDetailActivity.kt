package com.example.lucas.alnOnline.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.lucas.alnOnline.R
import kotlinx.android.synthetic.main.activity_new_detail.*

class NewDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_detail)

        title_new.text = intent.getStringExtra("title").toString()
        description.text = intent.getStringExtra("description").toString()
        date.text = intent.getStringExtra("datePosted").toString()

        Close.setOnClickListener( {
            val intent = Intent(this, UserNewsListActivity::class.java)
            startActivity(intent)
        })
    }
}
