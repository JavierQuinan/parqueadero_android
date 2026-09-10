package io.github.javierquinan.parking.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.github.javierquinan.parking.core.network.ApiConfig
import org.json.JSONObject

class VolleyParkingRepository(context: Context) : ParkingRepository {
    private val queue = Volley.newRequestQueue(context.applicationContext)

    override fun create(input: ParkingRecordInput, callback: (ParkingResult<String>) -> Unit) {
        request(payload("Insertar", input)) { response ->
            callback(messageResult(response))
        }
    }

    override fun list(callback: (ParkingResult<List<ParkingRecord>>) -> Unit) {
        request(JSONObject().put("accion", "consultar")) { response ->
            runCatching {
                if (!response.getBoolean("estado")) error(response.optString("mensaje", "No fue posible consultar los registros."))
                val rows = response.getJSONArray("autos")
                List(rows.length()) { index -> rows.getJSONObject(index).toRecord() }
            }.fold(
                onSuccess = { callback(ParkingResult.Success(it)) },
                onFailure = { callback(ParkingResult.Failure(it.message ?: "Respuesta inválida.", it)) }
            )
        }
    }

    override fun get(code: String, callback: (ParkingResult<ParkingRecord>) -> Unit) {
        request(JSONObject().put("accion", "Datos").put("codigo", code)) { response ->
            runCatching {
                if (!response.getBoolean("estado")) error(response.optString("mensaje", "Registro no encontrado."))
                response.getJSONArray("auto").getJSONObject(0).toRecord()
            }.fold(
                onSuccess = { callback(ParkingResult.Success(it)) },
                onFailure = { callback(ParkingResult.Failure(it.message ?: "Respuesta inválida.", it)) }
            )
        }
    }

    override fun checkout(
        code: String,
        input: ParkingRecordInput,
        totalFee: Double,
        callback: (ParkingResult<String>) -> Unit
    ) {
        val body = payload("Actualizar", input)
            .put("codigo", code)
            .put("tarifa_total", totalFee)
            .put("estado", 0)
        request(body) { response -> callback(messageResult(response)) }
    }

    private fun request(body: JSONObject, onResponse: (JSONObject) -> Unit) {
        val endpoint = ApiConfig.endpointOrNull()
        if (endpoint == null) {
            onResponse(JSONObject().put("estado", false).put("mensaje", "El endpoint de la API no está configurado."))
            return
        }
        queue.add(
            JsonObjectRequest(
                Request.Method.POST,
                endpoint,
                body,
                onResponse,
                { error ->
                    onResponse(
                        JSONObject()
                            .put("estado", false)
                            .put("mensaje", error.message ?: "Error de red.")
                    )
                }
            )
        )
    }

    private fun payload(action: String, input: ParkingRecordInput) = JSONObject()
        .put("accion", action)
        .put("placa", input.plate)
        .put("modelo", input.model)
        .put("anio", input.year)
        .put("color", input.color)
        .put("fecha", input.date)
        .put("entrada", input.entryTime)
        .put("salida", input.exitTime)

    private fun messageResult(response: JSONObject): ParkingResult<String> {
        val message = response.optString("mensaje", "Operación completada.")
        return if (response.optBoolean("estado", true)) {
            ParkingResult.Success(message)
        } else {
            ParkingResult.Failure(message)
        }
    }

    private fun JSONObject.toRecord() = ParkingRecord(
        code = getString("codigo"),
        plate = getString("placa"),
        model = getString("modelo"),
        year = optString("anio"),
        color = optString("color"),
        date = getString("fecha"),
        entryTime = getString("entrada"),
        exitTime = optString("salida")
    )
}
