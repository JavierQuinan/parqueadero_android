package io.github.javierquinan.parking.data

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class ParkingJsonContractTest {
    private val input = ParkingRecordInput("DEMO-001", "Sedan", "2024", "Azul", "2026-09-30", "08:00", "10:00")
    private fun row() = JSONObject().put("codigo", "demo-1").put("placa", input.plate)
        .put("modelo", input.model).put("fecha", input.date).put("entrada", input.entryTime)
    private fun response(key: String, rows: JSONArray) = JSONObject().put("estado", true).put(key, rows)

    @Test fun `create preserves action and all legacy fields`() {
        val body = ParkingJsonContract.createPayload(input)
        assertEquals(setOf("accion", "placa", "modelo", "anio", "color", "fecha", "entrada", "salida"), body.keys().asSequence().toSet())
        assertEquals("Insertar", body.getString("accion"))
        assertEquals("DEMO-001", body.getString("placa"))
        assertEquals("2024", body.getString("anio"))
        assertEquals("10:00", body.getString("salida"))
    }
    @Test fun `list and detail use distinct action contracts`() {
        assertEquals("consultar", ParkingJsonContract.listPayload().getString("accion"))
        val body = ParkingJsonContract.detailPayload("demo-1")
        assertEquals("Datos", body.getString("accion"))
        assertEquals("demo-1", body.getString("codigo"))
    }
    @Test fun `checkout carries fee code and numeric closed state`() {
        val body = ParkingJsonContract.checkoutPayload("demo-1", input, 4.5)
        assertEquals("Actualizar", body.getString("accion"))
        assertEquals("demo-1", body.getString("codigo"))
        assertEquals(4.5, body.getDouble("tarifa_total"), 0.0)
        assertEquals(0, body.getInt("estado"))
    }
    @Test fun `explicit server success preserves message`() {
        assertEquals(ParkingResult.Success("creado"), ParkingJsonContract.messageResult(JSONObject().put("estado", true).put("mensaje", "creado")))
    }
    @Test fun `missing null false and invalid state never become success`() {
        listOf(JSONObject(), JSONObject().put("estado", JSONObject.NULL), JSONObject().put("estado", false), JSONObject().put("estado", "invalid")).forEach {
            assertTrue(ParkingJsonContract.messageResult(it) is ParkingResult.Failure)
        }
    }
    @Test fun `server rejection preserves its explanation`() {
        val result = ParkingJsonContract.messageResult(JSONObject().put("estado", false).put("mensaje", "rechazado"))
        assertEquals("rechazado", (result as ParkingResult.Failure).message)
    }
    @Test fun `list maps required fields and optional defaults`() {
        val result = ParkingJsonContract.listResult(response("autos", JSONArray().put(row())))
        val record = (result as ParkingResult.Success).value.single()
        assertEquals("demo-1", record.code)
        assertEquals(input.plate, record.plate)
        assertEquals("", record.exitTime)
        assertEquals("", record.year)
    }
    @Test fun `empty list is a successful result`() {
        assertEquals(ParkingResult.Success(emptyList<ParkingRecord>()), ParkingJsonContract.listResult(response("autos", JSONArray())))
    }
    @Test fun `malformed list or missing required field fails`() {
        listOf(JSONObject().put("estado", true), response("autos", JSONArray().put(row().apply { remove("placa") })), JSONObject().put("estado", false)).forEach {
            assertTrue(ParkingJsonContract.listResult(it) is ParkingResult.Failure)
        }
    }
    @Test fun `detail uses singular auto array`() {
        val result = ParkingJsonContract.detailResult(response("auto", JSONArray().put(row())))
        assertEquals("demo-1", (result as ParkingResult.Success).value.code)
    }
    @Test fun `empty or malformed detail is a failure`() {
        listOf(response("auto", JSONArray()), response("autos", JSONArray().put(row())), JSONObject()).forEach {
            assertTrue(ParkingJsonContract.detailResult(it) is ParkingResult.Failure)
        }
    }
}
