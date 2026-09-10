package io.github.javierquinan.parking.data

interface ParkingRepository {
    fun create(input: ParkingRecordInput, callback: (ParkingResult<String>) -> Unit)
    fun list(callback: (ParkingResult<List<ParkingRecord>>) -> Unit)
    fun get(code: String, callback: (ParkingResult<ParkingRecord>) -> Unit)
    fun checkout(
        code: String,
        input: ParkingRecordInput,
        totalFee: Double,
        callback: (ParkingResult<String>) -> Unit
    )
}
