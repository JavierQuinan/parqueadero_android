package io.github.javierquinan.parking.data.remote

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.github.javierquinan.parking.core.network.ApiConfig
import io.github.javierquinan.parking.core.network.ApiError
import io.github.javierquinan.parking.core.network.ApiResult
import io.github.javierquinan.parking.data.remote.model.LoginRequest
import io.github.javierquinan.parking.data.remote.model.LoginResponse
import io.github.javierquinan.parking.data.remote.model.OperationResponse
import io.github.javierquinan.parking.data.remote.model.ParkingRecord
import io.github.javierquinan.parking.data.remote.model.ParkingRecordDraft
import io.github.javierquinan.parking.data.remote.model.ParkingRecordUpdate
import org.json.JSONException
import org.json.JSONObject

class LegacyParkingApiClient(
    context: Context,
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)
) {
    fun login(request: LoginRequest, callback: (ApiResult<LoginResponse>) -> Unit) {
        post(request.toJson(), callback) { response ->
            val authenticated = response.getString("estado").toInt() == 1
            LoginResponse(
                authenticated = authenticated,
                personId = if (authenticated && response.has("cod_persona")) {
                    response.getInt("cod_persona")
                } else {
                    null
                },
                message = response.optString("mensaje").takeIf { it.isNotBlank() }
            )
        }
    }

    fun createRecord(
        draft: ParkingRecordDraft,
        callback: (ApiResult<OperationResponse>) -> Unit
    ) {
        post(draft.toCreateJson(), callback, ::parseOperationResponse)
    }

    fun listRecords(callback: (ApiResult<List<ParkingRecord>>) -> Unit) {
        val payload = JSONObject().apply { put("accion", "consultar") }
        post(payload, callback) { response ->
            if (!response.getBoolean("estado")) {
                throw ApiRejectedException(response.optString("mensaje", "No fue posible consultar registros."))
            }

            val records = response.getJSONArray("autos")
            buildList {
                for (index in 0 until records.length()) {
                    add(parseRecord(records.getJSONObject(index)))
                }
            }
        }
    }

    fun getRecord(code: String, callback: (ApiResult<ParkingRecord>) -> Unit) {
        val payload = JSONObject().apply {
            put("accion", "Datos")
            put("codigo", code)
        }
        post(payload, callback) { response ->
            if (!response.getBoolean("estado")) {
                throw ApiRejectedException(response.optString("mensaje", "No fue posible consultar el registro."))
            }

            val records = response.getJSONArray("auto")
            if (records.length() == 0) {
                throw JSONException("Response field 'auto' is empty")
            }
            parseRecord(records.getJSONObject(0))
        }
    }

    fun updateRecord(
        update: ParkingRecordUpdate,
        callback: (ApiResult<OperationResponse>) -> Unit
    ) {
        post(update.toJson(), callback, ::parseOperationResponse)
    }

    private fun parseOperationResponse(response: JSONObject): OperationResponse {
        val successful = response.getBoolean("estado")
        val message = response.optString("mensaje").ifBlank {
            if (successful) "Operación completada." else "La operación fue rechazada."
        }
        return OperationResponse(successful = successful, message = message)
    }

    private fun parseRecord(record: JSONObject): ParkingRecord = ParkingRecord(
        code = record.getString("codigo"),
        plate = record.getString("placa"),
        model = record.getString("modelo"),
        year = record.getString("anio"),
        color = record.getString("color"),
        date = record.getString("fecha"),
        entryTime = record.getString("entrada"),
        exitTime = record.getString("salida")
    )

    private fun <T> post(
        payload: JSONObject,
        callback: (ApiResult<T>) -> Unit,
        parser: (JSONObject) -> T
    ) {
        val endpoint = ApiConfig.endpointOrNull()
        if (endpoint == null) {
            callback(ApiResult.Failure(ApiError.Configuration()))
            return
        }

        val request = JsonObjectRequest(
            Request.Method.POST,
            endpoint,
            payload,
            { response ->
                try {
                    callback(ApiResult.Success(parser(response)))
                } catch (rejected: ApiRejectedException) {
                    callback(ApiResult.Failure(ApiError.Rejected(rejected.message.orEmpty())))
                } catch (exception: JSONException) {
                    callback(
                        ApiResult.Failure(
                            ApiError.InvalidResponse(detail = exception.message)
                        )
                    )
                } catch (exception: NumberFormatException) {
                    callback(
                        ApiResult.Failure(
                            ApiError.InvalidResponse(detail = exception.message)
                        )
                    )
                }
            },
            { error ->
                callback(
                    ApiResult.Failure(
                        ApiError.Network(detail = error.message)
                    )
                )
            }
        )

        requestQueue.add(request)
    }

    private class ApiRejectedException(message: String) : IllegalStateException(message)
}
