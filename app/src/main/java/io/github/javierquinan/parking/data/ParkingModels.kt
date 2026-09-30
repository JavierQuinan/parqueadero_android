package io.github.javierquinan.parking.data

data class ParkingRecord(
    val code: String,
    val plate: String,
    val model: String,
    val year: String,
    val color: String,
    val date: String,
    val entryTime: String,
    val exitTime: String
)

data class ParkingRecordInput(
    val plate: String,
    val model: String,
    val year: String,
    val color: String,
    val date: String,
    val entryTime: String,
    val exitTime: String
)

sealed interface ParkingResult<out T> {
    data class Success<T>(val value: T) : ParkingResult<T>
    data class Failure(val message: String, val cause: Throwable? = null) : ParkingResult<Nothing>
}
