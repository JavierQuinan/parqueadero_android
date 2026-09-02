package io.github.javierquinan.parking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.github.javierquinan.parking.core.network.ApiConfig
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Locale

class ParkingManagementActivity : AppCompatActivity() {
    private val recordCodes = ArrayList<String>()

    private lateinit var plateInput: EditText
    private lateinit var modelInput: EditText
    private lateinit var yearInput: EditText
    private lateinit var colorInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var entryTimeInput: EditText
    private lateinit var exitTimeInput: EditText
    private lateinit var recordCodeInput: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_management)

        plateInput = findViewById(R.id.txt_placa)
        modelInput = findViewById(R.id.txt_modelo)
        yearInput = findViewById(R.id.txt_año)
        colorInput = findViewById(R.id.txt_color)
        dateInput = findViewById(R.id.txt_fecha)
        entryTimeInput = findViewById(R.id.txt_entrada)
        exitTimeInput = findViewById(R.id.txt_salida)
        recordCodeInput = findViewById(R.id.txt_dato)

        val recordsList = findViewById<ListView>(R.id.lista)
        recordsList.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedItem = adapterView.getItemAtPosition(position).toString()
            Log.d("ParkingRecord", "Selected item: $selectedItem")
            consultRecord(recordCodes[position])
        }

        findViewById<Button>(R.id.btn_consultar).setOnClickListener {
            consultRecords(recordsList)
        }

        findViewById<Button>(R.id.btn_ingresar).setOnClickListener {
            insertRecord(
                plate = plateInput.text.toString(),
                model = modelInput.text.toString(),
                year = yearInput.text.toString(),
                color = colorInput.text.toString(),
                date = dateInput.text.toString(),
                entryTime = entryTimeInput.text.toString(),
                exitTime = exitTimeInput.text.toString()
            )
        }

        findViewById<Button>(R.id.btn_modificar).setOnClickListener {
            updateRecord()
        }
    }

    private fun insertRecord(
        plate: String,
        model: String,
        year: String,
        color: String,
        date: String,
        entryTime: String,
        exitTime: String
    ) {
        val url = apiEndpointOrNotify() ?: return
        val payload = JSONObject().apply {
            put("accion", "Insertar")
            put("placa", plate)
            put("modelo", model)
            put("anio", year)
            put("color", color)
            put("fecha", date)
            put("entrada", entryTime)
            put("salida", exitTime)
        }

        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            payload,
            { response ->
                try {
                    Toast.makeText(
                        applicationContext,
                        response.getString("mensaje"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (exception: JSONException) {
                    Toast.makeText(applicationContext, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        )
        requestQueue.add(request)
    }

    private fun consultRecords(recordsList: ListView) {
        val displayRows = ArrayList<String>()
        val url = apiEndpointOrNotify() ?: return
        val payload = JSONObject().apply {
            put("accion", "consultar")
        }

        recordCodes.clear()
        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            payload,
            { response ->
                try {
                    if (response.getBoolean("estado")) {
                        val records = response.getJSONArray("autos")
                        for (index in 0 until records.length()) {
                            val row = records.getJSONObject(index)
                            recordCodes.add(row.getString("codigo"))
                            displayRows.add(
                                listOf(
                                    row.getString("placa"),
                                    row.getString("modelo"),
                                    row.getString("fecha"),
                                    row.getString("entrada")
                                ).joinToString(" ")
                            )
                        }

                        recordsList.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            displayRows
                        )
                    } else {
                        Toast.makeText(
                            applicationContext,
                            response.getString("mensaje"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (exception: JSONException) {
                    Toast.makeText(applicationContext, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(request)
    }

    private fun consultRecord(code: String) {
        val url = apiEndpointOrNotify() ?: return
        val payload = JSONObject().apply {
            put("accion", "Datos")
            put("codigo", code)
        }

        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            payload,
            { response ->
                try {
                    if (response.getBoolean("estado")) {
                        val record = response.getJSONArray("auto").getJSONObject(0)
                        recordCodeInput.setText(record.getString("codigo"))
                        plateInput.setText(record.getString("placa"))
                        modelInput.setText(record.getString("modelo"))
                        yearInput.setText(record.getString("anio"))
                        colorInput.setText(record.getString("color"))
                        dateInput.setText(record.getString("fecha"))
                        entryTimeInput.setText(record.getString("entrada"))
                        exitTimeInput.setText(record.getString("salida"))
                    } else {
                        Toast.makeText(
                            applicationContext,
                            response.getString("mensaje"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (exception: JSONException) {
                    Toast.makeText(applicationContext, exception.toString(), Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        )
        requestQueue.add(request)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateRecord() {
        val plate = plateInput.text.toString()
        val model = modelInput.text.toString()
        val year = yearInput.text.toString()
        val color = colorInput.text.toString()
        val date = dateInput.text.toString()
        val entryTime = entryTimeInput.text.toString()
        val exitTime = exitTimeInput.text.toString()
        val code = recordCodeInput.text.toString()

        val parsedEntryTime = parseTime(entryTime)
        val parsedExitTime = parseTime(exitTime)
        val totalFee = calculateTotalFee(parsedEntryTime, parsedExitTime, HOURLY_RATE)

        val url = apiEndpointOrNotify() ?: return
        val payload = JSONObject().apply {
            put("accion", "Actualizar")
            put("placa", plate)
            put("modelo", model)
            put("anio", year)
            put("color", color)
            put("fecha", date)
            put("entrada", entryTime)
            put("salida", exitTime)
            put("codigo", code)
            put("tarifa_total", totalFee)
            put("estado", 0)
        }

        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            payload,
            { response ->
                try {
                    Toast.makeText(
                        applicationContext,
                        response.getString("mensaje"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (exception: JSONException) {
                    Toast.makeText(applicationContext, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        )
        requestQueue.add(request)

        startActivity(
            Intent(this, FeeSummaryActivity::class.java)
                .putExtra(EXTRA_TOTAL_FEE, totalFee)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateTotalFee(
        entryTime: LocalTime,
        exitTime: LocalTime,
        hourlyRate: Double
    ): Double {
        val elapsedHours = ChronoUnit.HOURS.between(entryTime, exitTime)
        return elapsedHours * hourlyRate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseTime(value: String): LocalTime {
        val normalized = if (value.endsWith("AM") || value.endsWith("PM")) {
            val twelveHourFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val twentyFourHourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            twentyFourHourFormat.format(twelveHourFormat.parse(value))
        } else {
            value
        }

        return LocalTime.parse(normalized)
    }

    private fun apiEndpointOrNotify(): String? {
        val endpoint = ApiConfig.endpointOrNull()
        if (endpoint == null) {
            Toast.makeText(
                this,
                "El endpoint de la API no está configurado.",
                Toast.LENGTH_LONG
            ).show()
        }
        return endpoint
    }

    companion object {
        const val EXTRA_TOTAL_FEE = "TARIFA_TOTAL"
        private const val HOURLY_RATE = 1.0
    }
}
