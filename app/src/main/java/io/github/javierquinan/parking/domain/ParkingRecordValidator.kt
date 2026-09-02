package io.github.javierquinan.parking.domain

object ParkingRecordValidator {
    data class Input(
        val plate: String,
        val model: String,
        val year: String,
        val color: String,
        val date: String,
        val entryTime: String,
        val exitTime: String
    )

    data class ValidatedInput(
        val plate: String,
        val model: String,
        val year: String,
        val color: String,
        val date: String,
        val entryTime: String,
        val exitTime: String
    )

    fun validateForCreate(input: Input): Result<ValidatedInput> = runCatching {
        validateBase(input, requireExit = false)
    }

    fun validateForCheckout(input: Input): Result<ValidatedInput> = runCatching {
        validateBase(input, requireExit = true)
    }

    private fun validateBase(input: Input, requireExit: Boolean): ValidatedInput {
        val plate = input.plate.trim().uppercase()
        require(PLATE_PATTERN.matches(plate)) {
            "Plate must contain 5 to 10 letters, numbers or hyphens"
        }

        val model = input.model.trim()
        require(model.isNotEmpty()) { "Model is required" }

        val year = input.year.trim()
        require(YEAR_PATTERN.matches(year)) { "Year must contain four digits" }

        val color = input.color.trim()
        require(color.isNotEmpty()) { "Color is required" }

        val date = input.date.trim()
        require(DATE_PATTERN.matches(date)) { "Date must use YYYY-MM-DD" }

        val entryTime = input.entryTime.trim()
        ParkingFeeCalculator.parseMinutes(entryTime)

        val exitTime = input.exitTime.trim()
        if (requireExit) {
            require(exitTime.isNotEmpty()) { "Exit time is required" }
            ParkingFeeCalculator.parseMinutes(exitTime)
        } else if (exitTime.isNotEmpty()) {
            ParkingFeeCalculator.parseMinutes(exitTime)
        }

        return ValidatedInput(
            plate = plate,
            model = model,
            year = year,
            color = color,
            date = date,
            entryTime = entryTime,
            exitTime = exitTime
        )
    }

    private val PLATE_PATTERN = Regex("^[A-Z0-9-]{5,10}$")
    private val YEAR_PATTERN = Regex("^\\d{4}$")
    private val DATE_PATTERN = Regex("^\\d{4}-\\d{2}-\\d{2}$")
}
