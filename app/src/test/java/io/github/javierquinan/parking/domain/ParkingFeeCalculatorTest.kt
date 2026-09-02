package io.github.javierquinan.parking.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ParkingFeeCalculatorTest {
    @Test
    fun `calculates complete elapsed hours using 24-hour input`() {
        val result = ParkingFeeCalculator.calculate("08:00", "11:45", 1.5)

        assertEquals(225, result.elapsedMinutes)
        assertEquals(3, result.billableHours)
        assertEquals(4.5, result.totalFee, 0.0)
    }

    @Test
    fun `supports 12-hour AM PM input`() {
        val result = ParkingFeeCalculator.calculate("08:15 AM", "01:20 PM", 2.0)

        assertEquals(305, result.elapsedMinutes)
        assertEquals(5, result.billableHours)
        assertEquals(10.0, result.totalFee, 0.0)
    }

    @Test
    fun `keeps original full-hour billing behavior for partial hour`() {
        val result = ParkingFeeCalculator.calculate("09:00", "09:59", 2.0)

        assertEquals(59, result.elapsedMinutes)
        assertEquals(0, result.billableHours)
        assertEquals(0.0, result.totalFee, 0.0)
    }

    @Test
    fun `rejects exit earlier than entry for same date`() {
        assertThrows(IllegalArgumentException::class.java) {
            ParkingFeeCalculator.calculate("18:00", "17:00", 1.0)
        }
    }

    @Test
    fun `rejects malformed time`() {
        assertThrows(IllegalArgumentException::class.java) {
            ParkingFeeCalculator.calculate("8pm", "21:00", 1.0)
        }
    }

    @Test
    fun `rejects negative rate`() {
        assertThrows(IllegalArgumentException::class.java) {
            ParkingFeeCalculator.calculate("08:00", "09:00", -1.0)
        }
    }
}
