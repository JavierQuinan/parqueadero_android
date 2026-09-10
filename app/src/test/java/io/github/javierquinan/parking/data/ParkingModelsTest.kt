package io.github.javierquinan.parking.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkingModelsTest {
    @Test
    fun `record input preserves validated transport values`() {
        val input = ParkingRecordInput("ABC-123", "Sedan", "2024", "Blue", "2026-09-09", "08:00", "")
        assertEquals("ABC-123", input.plate)
        assertEquals("08:00", input.entryTime)
    }

    @Test
    fun `parking result exposes typed success`() {
        val result: ParkingResult<String> = ParkingResult.Success("ok")
        assertTrue(result is ParkingResult.Success)
        assertEquals("ok", (result as ParkingResult.Success).value)
    }
}
