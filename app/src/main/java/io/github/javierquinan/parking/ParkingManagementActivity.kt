package io.github.javierquinan.parking

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.javierquinan.parking.data.ParkingRecord
import io.github.javierquinan.parking.data.ParkingRecordInput
import io.github.javierquinan.parking.data.ParkingRepository
import io.github.javierquinan.parking.data.ParkingResult
import io.github.javierquinan.parking.data.VolleyParkingRepository
import io.github.javierquinan.parking.domain.ParkingFeeCalculator
import io.github.javierquinan.parking.domain.ParkingRecordValidator

class ParkingManagementActivity : AppCompatActivity() {
    private val recordCodes = ArrayList<String>()
    private lateinit var repository: ParkingRepository
    private lateinit var recordsList: ListView

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
        repository = VolleyParkingRepository(this)

        plateInput = findViewById(R.id.txt_placa)
        modelInput = findViewById(R.id.txt_modelo)
        yearInput = findViewById(R.id.txt_año)
        colorInput = findViewById(R.id.txt_color)
        dateInput = findViewById(R.id.txt_fecha)
        entryTimeInput = findViewById(R.id.txt_entrada)
        exitTimeInput = findViewById(R.id.txt_salida)
        recordCodeInput = findViewById(R.id.txt_dato)
        recordsList = findViewById(R.id.lista)

        recordsList.setOnItemClickListener { _, _, position, _ ->
            recordCodes.getOrNull(position)?.let(::consultRecord)
        }
        findViewById<Button>(R.id.btn_consultar).setOnClickListener { consultRecords() }
        findViewById<Button>(R.id.btn_ingresar).setOnClickListener { insertRecord(currentInput()) }
        findViewById<Button>(R.id.btn_modificar).setOnClickListener { updateRecord() }
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
        val validated = ParkingRecordValidator.validateForCreate(input).getOrElse {
            showError(it.message); return
        }
        repository.create(validated.toTransport()) { result ->
            when (result) {
                is ParkingResult.Success -> showMessage(result.value)
                is ParkingResult.Failure -> showError(result.message)
            }
        }
    }

    private fun consultRecords() {
        repository.list { result ->
            when (result) {
                is ParkingResult.Success -> {
                    recordCodes.clear()
                    recordCodes.addAll(result.value.map { it.code })
                    recordsList.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        result.value.map { "${it.plate} ${it.model} ${it.date} ${it.entryTime}" }
                    )
                }
                is ParkingResult.Failure -> showError(result.message)
            }
        }
    }

    private fun consultRecord(code: String) {
        repository.get(code) { result ->
            when (result) {
                is ParkingResult.Success -> bindRecord(result.value)
                is ParkingResult.Failure -> showError(result.message)
            }
        }
    }

    private fun updateRecord() {
        val code = recordCodeInput.text.toString().trim()
        if (code.isEmpty()) {
            showError("Seleccione un registro antes de marcar la salida.")
            return
        }
        val validated = ParkingRecordValidator.validateForCheckout(currentInput()).getOrElse {
            showError(it.message); return
        }
        val fee = runCatching {
            ParkingFeeCalculator.calculate(validated.entryTime, validated.exitTime, HOURLY_RATE)
        }.getOrElse {
            showError(it.message); return
        }
        repository.checkout(code, validated.toTransport(), fee.totalFee) { result ->
            when (result) {
                is ParkingResult.Success -> {
                    showMessage(result.value)
                    startActivity(Intent(this, FeeSummaryActivity::class.java).putExtra(EXTRA_TOTAL_FEE, fee.totalFee))
                }
                is ParkingResult.Failure -> showError(result.message)
            }
        }
    }

    private fun ParkingRecordValidator.ValidatedInput.toTransport() = ParkingRecordInput(
        plate, model, year, color, date, entryTime, exitTime
    )

    private fun bindRecord(record: ParkingRecord) {
        recordCodeInput.setText(record.code)
        plateInput.setText(record.plate)
        modelInput.setText(record.model)
        yearInput.setText(record.year)
        colorInput.setText(record.color)
        dateInput.setText(record.date)
        entryTimeInput.setText(record.entryTime)
        exitTimeInput.setText(record.exitTime)
    }

    private fun showMessage(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun showError(message: String?) =
        Toast.makeText(this, message ?: "Operación de parqueo inválida.", Toast.LENGTH_LONG).show()

    companion object {
        const val EXTRA_TOTAL_FEE = "TARIFA_TOTAL"
        private const val HOURLY_RATE = 1.0
    }
}
