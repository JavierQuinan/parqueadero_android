package io.github.javierquinan.parking.domain

/**
 * Pure Kotlin parking-fee calculation extracted from Android UI code.
 *
 * Current pricing rule preserves the prototype's original behavior:
 * only complete elapsed hours are billed at the configured hourly rate.
 * Exit time must be on or after entry time for the same parking date.
 */
object ParkingFeeCalculator {
    data class Result(
        val elapsedMinutes: Int,
        val billableHours: Int,
        val totalFee: Double
    )

    fun calculate(entryTime: String, exitTime: String, hourlyRate: Double): Result {
        require(hourlyRate >= 0.0) { "Hourly rate cannot be negative" }

        val entryMinutes = parseMinutes(entryTime)
        val exitMinutes = parseMinutes(exitTime)
        require(exitMinutes >= entryMinutes) {
            "Exit time cannot be earlier than entry time for the same date"
        }

        val elapsedMinutes = exitMinutes - entryMinutes
        val billableHours = elapsedMinutes / MINUTES_PER_HOUR

        return Result(
            elapsedMinutes = elapsedMinutes,
            billableHours = billableHours,
            totalFee = billableHours * hourlyRate
        )
    }

    internal fun parseMinutes(rawValue: String): Int {
        val value = rawValue.trim().uppercase()
        require(value.isNotEmpty()) { "Time is required" }

        val amPmMatch = TWELVE_HOUR_PATTERN.matchEntire(value)
        if (amPmMatch != null) {
            val hour = amPmMatch.groupValues[1].toInt()
            val minute = amPmMatch.groupValues[2].toInt()
            val period = amPmMatch.groupValues[3]

            require(hour in 1..12) { "Invalid 12-hour time" }
            require(minute in 0..59) { "Invalid minute" }

            val normalizedHour = when {
                period == "AM" && hour == 12 -> 0
                period == "PM" && hour != 12 -> hour + 12
                else -> hour
            }

            return normalizedHour * MINUTES_PER_HOUR + minute
        }

        val twentyFourHourMatch = TWENTY_FOUR_HOUR_PATTERN.matchEntire(value)
            ?: throw IllegalArgumentException("Time must use HH:mm or hh:mm AM/PM")

        val hour = twentyFourHourMatch.groupValues[1].toInt()
        val minute = twentyFourHourMatch.groupValues[2].toInt()

        require(hour in 0..23) { "Invalid 24-hour time" }
        require(minute in 0..59) { "Invalid minute" }

        return hour * MINUTES_PER_HOUR + minute
    }

    private const val MINUTES_PER_HOUR = 60
    private val TWELVE_HOUR_PATTERN = Regex("^(\\d{1,2}):(\\d{2})\\s*(AM|PM)$")
    private val TWENTY_FOUR_HOUR_PATTERN = Regex("^(\\d{1,2}):(\\d{2})$")
}
