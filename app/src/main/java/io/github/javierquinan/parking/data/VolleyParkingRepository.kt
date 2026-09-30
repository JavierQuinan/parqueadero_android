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
        request(ParkingJsonContract.createPayload(input)) { response ->
            callback(ParkingJsonContract.messageResult(response))
        }
    }

    override fun list(callback: (ParkingResult<List<ParkingRecord>>) -> Unit) {
        request(ParkingJsonContract.listPayload()) { response ->
            callback(ParkingJsonContract.listResult(response))
        }
    }

    override fun get(code: String, callback: (ParkingResult<ParkingRecord>) -> Unit) {
        request(ParkingJsonContract.detailPayload(code)) { response ->
            callback(ParkingJsonContract.detailResult(response))
        }
    }

    override fun checkout(
        code: String,
        input: ParkingRecordInput,
        totalFee: Double,
        callback: (ParkingResult<String>) -> Unit
    ) {
        request(ParkingJsonContract.checkoutPayload(code, input, totalFee)) { response ->
            callback(ParkingJsonContract.messageResult(response))
        }
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

}
