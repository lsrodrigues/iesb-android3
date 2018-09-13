package com.example.lucas.alnOnline.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.lucas.alnOnline.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(object : Runnable {

            override fun run() {
                val i = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(i)
                this.finish()
            }

            private fun finish() {}
        }, 5000)

    };
}
