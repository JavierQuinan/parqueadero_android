package io.github.javierquinan.parking.domain.validation

import io.github.javierquinan.parking.data.remote.model.ParkingRecordDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkingInputValidatorTest {
    @Test
    fun validDraftIsNormalized() {
        val result = ParkingInputValidator.validate(
            validDraft().copy(plate = " abc-1234 ", model = " Corolla ")
        )

        assertTrue(result.isValid)
        assertEquals("ABC-1234", result.normalized?.plate)
        assertEquals("Corolla", result.normalized?.model)
    }

    @Test
    fun invalidPlateDateAndTimeAreRejected() {
        val result = ParkingInputValidator.validate(
            validDraft().copy(
                plate = "@@",
                date = "02/09/2026",
                entryTime = "27:61"
            )
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.size >= 3)
    }

    @Test
    fun twelveHourTimeIsAcceptedWhenWellFormed() {
        val result = ParkingInputValidator.validate(
            validDraft().copy(entryTime = "08:30 AM", exitTime = "05:45 PM")
        )

        assertTrue(result.isValid)
    }

    private fun validDraft() = ParkingRecordDraft(
        plate = "ABC-1234",
        model = "Corolla",
        year = "2024",
        color = "Blanco",
        date = "2026-09-02",
        entryTime = "08:30",
        exitTime = "17:45"
    )
}
