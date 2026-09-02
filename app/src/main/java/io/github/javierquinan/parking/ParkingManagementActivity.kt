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
import io.github.javierquinan.parking.core.network.ApiResult
import io.github.javierquinan.parking.data.remote.LegacyParkingApiClient
import io.github.javierquinan.parking.data.remote.model.ParkingRecord
import io.github.javierquinan.parking.data.remote.model.ParkingRecordDraft
import io.github.javierquinan.parking.data.remote.model.ParkingRecordUpdate
import io.github.javierquinan.parking.domain.validation.ParkingInputValidator
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Locale

class ParkingManagementActivity : AppCompatActivity() {
    private val recordCodes = ArrayList<String>()
    private val apiClient by lazy { LegacyParkingApiClient(this) }

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
            val draft = validatedDraftOrNotify() ?: return@setOnClickListener
            insertRecord(draft)
        }

        findViewById<Button>(R.id.btn_modificar).setOnClickListener {
            updateRecord()
        }
    }

    private fun insertRecord(draft: ParkingRecordDraft) {
        apiClient.createRecord(draft) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(this, result.value.message, Toast.LENGTH_SHORT).show()
                }

                is ApiResult.Failure -> showApiFailure(result)
            }
        }
    }

    private fun consultRecords(recordsList: ListView) {
        apiClient.listRecords { result ->
            when (result) {
                is ApiResult.Success -> {
                    recordCodes.clear()
                    recordCodes.addAll(result.value.map { it.code })

                    val displayRows = result.value.map { record ->
                        listOf(
                            record.plate,
                            record.model,
                            record.date,
                            record.entryTime
                        ).joinToString(" ")
                    }

                    recordsList.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        displayRows
                    )
                }

                is ApiResult.Failure -> showApiFailure(result)
            }
        }
    }

    private fun consultRecord(code: String) {
        apiClient.getRecord(code) { result ->
            when (result) {
                is ApiResult.Success -> populateRecord(result.value)
                is ApiResult.Failure -> showApiFailure(result)
            }
        }
    }

    private fun populateRecord(record: ParkingRecord) {
        recordCodeInput.setText(record.code)
        plateInput.setText(record.plate)
        modelInput.setText(record.model)
        yearInput.setText(record.year)
        colorInput.setText(record.color)
        dateInput.setText(record.date)
        entryTimeInput.setText(record.entryTime)
        exitTimeInput.setText(record.exitTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateRecord() {
        val code = recordCodeInput.text.toString().trim()
        if (code.isBlank()) {
            Toast.makeText(
                this,
                "Selecciona un registro antes de procesar la salida.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val draft = validatedDraftOrNotify() ?: return
        val parsedEntryTime = parseTime(draft.entryTime)
        val parsedExitTime = parseTime(draft.exitTime)
        val totalFee = calculateTotalFee(parsedEntryTime, parsedExitTime, HOURLY_RATE)
        val update = ParkingRecordUpdate(
            code = code,
            draft = draft,
            totalFee = totalFee
        )

        apiClient.updateRecord(update) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(this, result.value.message, Toast.LENGTH_SHORT).show()
                    if (result.value.successful) {
                        startActivity(
                            Intent(this, FeeSummaryActivity::class.java)
                                .putExtra(EXTRA_TOTAL_FEE, totalFee)
                        )
                    }
                }

                is ApiResult.Failure -> showApiFailure(result)
            }
        }
    }

    private fun validatedDraftOrNotify(): ParkingRecordDraft? {
        val candidate = ParkingRecordDraft(
            plate = plateInput.text.toString(),
            model = modelInput.text.toString(),
            year = yearInput.text.toString(),
            color = colorInput.text.toString(),
            date = dateInput.text.toString(),
            entryTime = entryTimeInput.text.toString(),
            exitTime = exitTimeInput.text.toString()
        )
        val validation = ParkingInputValidator.validate(candidate)

        if (!validation.isValid) {
            Toast.makeText(
                this,
                validation.errors.joinToString("\n"),
                Toast.LENGTH_LONG
            ).show()
            return null
        }

        return validation.normalized
    }

    private fun showApiFailure(result: ApiResult.Failure) {
        Toast.makeText(this, result.error.userMessage, Toast.LENGTH_LONG).show()
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
            val twelveHourFormat = SimpleDateFormat("hh:mm a", Locale.ROOT)
            val twentyFourHourFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
            twentyFourHourFormat.format(requireNotNull(twelveHourFormat.parse(value)))
        } else {
            value
        }

        return LocalTime.parse(normalized)
    }

    companion object {
        const val EXTRA_TOTAL_FEE = "TARIFA_TOTAL"
        private const val HOURLY_RATE = 1.0
    }
}
