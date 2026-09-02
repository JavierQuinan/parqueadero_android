package io.github.javierquinan.parking.domain.validation

import io.github.javierquinan.parking.data.remote.model.ParkingRecordDraft
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ValidationResult(
    val normalized: ParkingRecordDraft? = null,
    val errors: List<String> = emptyList()
) {
    val isValid: Boolean get() = normalized != null && errors.isEmpty()
}

object ParkingInputValidator {
    private val platePattern = Regex("^[A-Z0-9-]{5,12}$")
    private val yearPattern = Regex("^\\d{4}$")

    fun validate(draft: ParkingRecordDraft): ValidationResult {
        val normalized = draft.copy(
            plate = draft.plate.trim().uppercase(Locale.ROOT),
            model = draft.model.trim(),
            year = draft.year.trim(),
            color = draft.color.trim(),
            date = draft.date.trim(),
            entryTime = draft.entryTime.trim().uppercase(Locale.ROOT),
            exitTime = draft.exitTime.trim().uppercase(Locale.ROOT)
        )

        val errors = buildList {
            if (!platePattern.matches(normalized.plate)) {
                add("La placa debe contener entre 5 y 12 caracteres alfanuméricos o guiones.")
            }
            if (normalized.model.isBlank()) add("El modelo es obligatorio.")
            if (normalized.color.isBlank()) add("El color es obligatorio.")
            if (!isValidYear(normalized.year)) add("El año del vehículo no es válido.")
            if (!isStrictDate(normalized.date)) add("La fecha debe usar el formato yyyy-MM-dd.")
            if (!isValidTime(normalized.entryTime)) add("La hora de entrada no es válida.")
            if (!isValidTime(normalized.exitTime)) add("La hora de salida no es válida.")
        }

        return if (errors.isEmpty()) {
            ValidationResult(normalized = normalized)
        } else {
            ValidationResult(errors = errors)
        }
    }

    private fun isValidYear(value: String): Boolean {
        if (!yearPattern.matches(value)) return false
        val year = value.toIntOrNull() ?: return false
        val maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1
        return year in 1900..maxYear
    }

    private fun isStrictDate(value: String): Boolean =
        parsesStrictly(value, "yyyy-MM-dd")

    private fun isValidTime(value: String): Boolean =
        parsesStrictly(value, "HH:mm") || parsesStrictly(value, "hh:mm a")

    private fun parsesStrictly(value: String, pattern: String): Boolean {
        val formatter = SimpleDateFormat(pattern, Locale.ROOT).apply {
            isLenient = false
        }
        return try {
            val parsed = formatter.parse(value) ?: return false
            formatter.format(parsed) == value
        } catch (_: ParseException) {
            false
        }
    }
}
