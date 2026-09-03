package io.github.javierquinan.parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.github.javierquinan.parking.core.network.ApiConfig
import io.github.javierquinan.parking.domain.ParkingFeeCalculator
import io.github.javierquinan.parking.domain.ParkingRecordValidator
import org.json.JSONException
import org.json.JSONObject

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
            insertRecord(currentInput())
        }

        findViewById<Button>(R.id.btn_modificar).setOnClickListener {
            updateRecord()
        }
    }

    private fun currentInput() = ParkingRecordValidator.Input(
        plate = plateInput.text.toString(),
        model = modelInput.text.toString(),
        year = yearInput.text.toString(),
        color = colorInput.text.toString(),
        date = dateInput.text.toString(),
        entryTime = entryTimeInput.text.toString(),
        exitTime = exitTimeInput.text.toString()
    )

    private fun insertRecord(input: ParkingRecordValidator.Input) {
        val validated = ParkingRecordValidator.validateForCreate(input).getOrElse { error ->
            showValidationError(error)
            return
        }

        val url = apiEndpointOrNotify() ?: return
        val payload = JSONObject().apply {
            put("accion", "Insertar")
            put("placa", validated.plate)
            put("modelo", validated.model)
            put("anio", validated.year)
            put("color", validated.color)
            put("fecha", validated.date)
            put("entrada", validated.entryTime)
            put("salida", validated.exitTime)
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

    private fun updateRecord() {
        val code = recordCodeInput.text.toString().trim()
        if (code.isEmpty()) {
            Toast.makeText(this, "Seleccione un registro antes de marcar la salida.", Toast.LENGTH_LONG).show()
            return
        }

        val validated = ParkingRecordValidator.validateForCheckout(currentInput()).getOrElse { error ->
            showValidationError(error)
            return
        }

        val fee = runCatching {
            ParkingFeeCalculator.calculate(
                entryTime = validated.entryTime,
                exitTime = validated.exitTime,
                hourlyRate = HOURLY_RATE
            )
        }.getOrElse { error ->
            showValidationError(error)
            return
        }

        val url = apiEndpointOrNotify() ?: return
        val payload = JSONObject().apply {
            put("accion", "Actualizar")
            put("placa", validated.plate)
            put("modelo", validated.model)
            put("anio", validated.year)
            put("color", validated.color)
            put("fecha", validated.date)
            put("entrada", validated.entryTime)
            put("salida", validated.exitTime)
            put("codigo", code)
            put("tarifa_total", fee.totalFee)
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

                    startActivity(
                        Intent(this, FeeSummaryActivity::class.java)
                            .putExtra(EXTRA_TOTAL_FEE, fee.totalFee)
                    )
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

    private fun showValidationError(error: Throwable) {
        Toast.makeText(
            this,
            error.message ?: "Datos de parqueo inválidos.",
            Toast.LENGTH_LONG
        ).show()
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
