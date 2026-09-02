package io.github.javierquinan.parking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FeeSummaryActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fee_summary)

        val totalFee = intent.getDoubleExtra(ParkingManagementActivity.EXTRA_TOTAL_FEE, 0.0)
        findViewById<TextView>(R.id.lbls).text = "Tarifa Total: $totalFee"

        findViewById<Button>(R.id.btnRegresar).setOnClickListener {
            startActivity(Intent(this, ParkingManagementActivity::class.java))
            finish()
        }
    }
}
