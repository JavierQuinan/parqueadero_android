package io.github.javierquinan.parking.data.remote.model

import org.json.JSONObject

data class LoginRequest(
    val username: String,
    val password: String
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("accion", "consultarDato")
        put("usuario", username)
        put("clave", password)
    }
}

data class LoginResponse(
    val authenticated: Boolean,
    val personId: Int? = null,
    val message: String? = null
)

data class ParkingRecordDraft(
    val plate: String,
    val model: String,
    val year: String,
    val color: String,
    val date: String,
    val entryTime: String,
    val exitTime: String
) {
    fun toCreateJson(): JSONObject = JSONObject().apply {
        put("accion", "Insertar")
        put("placa", plate)
        put("modelo", model)
        put("anio", year)
        put("color", color)
        put("fecha", date)
        put("entrada", entryTime)
        put("salida", exitTime)
    }
}

data class ParkingRecord(
    val code: String,
    val plate: String,
    val model: String,
    val year: String,
    val color: String,
    val date: String,
    val entryTime: String,
    val exitTime: String
)

data class ParkingRecordUpdate(
    val code: String,
    val draft: ParkingRecordDraft,
    val totalFee: Double,
    val status: Int = 0
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("accion", "Actualizar")
        put("placa", draft.plate)
        put("modelo", draft.model)
        put("anio", draft.year)
        put("color", draft.color)
        put("fecha", draft.date)
        put("entrada", draft.entryTime)
        put("salida", draft.exitTime)
        put("codigo", code)
        put("tarifa_total", totalFee)
        put("estado", status)
    }
}

data class OperationResponse(
    val successful: Boolean,
    val message: String
)
