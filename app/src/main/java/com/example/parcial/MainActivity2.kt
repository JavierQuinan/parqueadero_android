package com.example.parcial

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity2 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val tarifaTotal = intent.getDoubleExtra("TARIFA_TOTAL", 0.0)
        val lbls = findViewById<TextView>(R.id.lbls)
        val btnRegresar = findViewById<Button>(R.id.btnRegresar)
        lbls.text = "Tarifa Total: $tarifaTotal"

        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}