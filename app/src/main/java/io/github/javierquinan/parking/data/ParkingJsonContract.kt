package io.github.javierquinan.parking.data

import org.json.JSONObject

/** Mapping for the existing action-based backend contract. */
internal object ParkingJsonContract {
    fun createPayload(input: ParkingRecordInput) = payload("Insertar", input)
    fun listPayload() = JSONObject().put("accion", "consultar")
    fun detailPayload(code: String) = JSONObject().put("accion", "Datos").put("codigo", code)
    fun checkoutPayload(code: String, input: ParkingRecordInput, totalFee: Double) =
        payload("Actualizar", input).put("codigo", code).put("tarifa_total", totalFee).put("estado", 0)

    fun messageResult(response: JSONObject): ParkingResult<String> {
        val success = response.optBoolean("estado", false)
        val message = response.optString(
            "mensaje",
            if (success) "Operación completada." else "Respuesta inválida o fallida del servidor."
        )
        return if (success) ParkingResult.Success(message) else ParkingResult.Failure(message)
    }

    fun listResult(response: JSONObject): ParkingResult<List<ParkingRecord>> = decode {
        requireSuccess(response, "No fue posible consultar los registros.")
        val rows = response.getJSONArray("autos")
        List(rows.length()) { rows.getJSONObject(it).toRecord() }
    }

    fun detailResult(response: JSONObject): ParkingResult<ParkingRecord> = decode {
        requireSuccess(response, "Registro no encontrado.")
        response.getJSONArray("auto").getJSONObject(0).toRecord()
    }

    private fun requireSuccess(response: JSONObject, fallback: String) {
        check(response.getBoolean("estado")) { response.optString("mensaje", fallback) }
    }

    private fun <T> decode(block: () -> T): ParkingResult<T> = runCatching(block).fold(
        onSuccess = { ParkingResult.Success(it) },
        onFailure = { ParkingResult.Failure(it.message ?: "Respuesta inválida.", it) }
    )

    private fun payload(action: String, input: ParkingRecordInput) = JSONObject()
        .put("accion", action).put("placa", input.plate).put("modelo", input.model)
        .put("anio", input.year).put("color", input.color).put("fecha", input.date)
        .put("entrada", input.entryTime).put("salida", input.exitTime)

    private fun JSONObject.toRecord() = ParkingRecord(
        code = getString("codigo"), plate = getString("placa"), model = getString("modelo"),
        year = optString("anio"), color = optString("color"), date = getString("fecha"),
        entryTime = getString("entrada"), exitTime = optString("salida")
    )
}
