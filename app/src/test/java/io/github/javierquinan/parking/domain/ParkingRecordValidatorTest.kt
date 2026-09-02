package io.github.javierquinan.parking.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkingRecordValidatorTest {
    @Test
    fun `normalizes a valid plate and preserves validated values`() {
        val result = ParkingRecordValidator.validate(
            ParkingRecordValidator.Input(
                plate = " abc-123 ",
                model = "Sedan",
                year = "2024",
                color = "Azul",
                date = "2026-09-02",
                entryTime = "08:00",
                exitTime = "10:00"
            )
        )

        assertTrue(result.isSuccess)
        assertEquals("ABC-123", result.getOrThrow().plate)
    }

    @Test
    fun `rejects malformed plate`() {
        val result = ParkingRecordValidator.validate(
            validInput().copy(plate = "A@1")
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `rejects non four-digit year`() {
        val result = ParkingRecordValidator.validate(
            validInput().copy(year = "24")
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `rejects malformed date format`() {
        val result = ParkingRecordValidator.validate(
            validInput().copy(date = "02/09/2026")
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `rejects malformed entry time`() {
        val result = ParkingRecordValidator.validate(
            validInput().copy(entryTime = "eight")
        )

        assertTrue(result.isFailure)
    }

    private fun validInput() = ParkingRecordValidator.Input(
        plate = "ABC-123",
        model = "Sedan",
        year = "2024",
        color = "Azul",
        date = "2026-09-02",
        entryTime = "08:00",
        exitTime = "10:00"
    )
}
