package com.example.suicoffeemachineapp

import android.content.Intent
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEspresso = findViewById<LinearLayout>(R.id.btnEspresso)
        val btnLungo = findViewById<LinearLayout>(R.id.btnLungo)
        btnEspresso.setOnClickListener{
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
        btnLungo.setOnClickListener{
            //do something else
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
    }
}