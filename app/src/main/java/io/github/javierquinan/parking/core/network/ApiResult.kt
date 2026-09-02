package io.github.javierquinan.parking.core.network

sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    data class Failure(val error: ApiError) : ApiResult<Nothing>
}

sealed class ApiError(open val userMessage: String) {
    data class Configuration(
        override val userMessage: String = "El endpoint de la API no está configurado."
    ) : ApiError(userMessage)

    data class Network(
        val detail: String? = null,
        override val userMessage: String = "No fue posible completar la solicitud de red."
    ) : ApiError(userMessage)

    data class InvalidResponse(
        val detail: String? = null,
        override val userMessage: String = "La respuesta del servidor no tiene el formato esperado."
    ) : ApiError(userMessage)

    data class Rejected(
        override val userMessage: String
    ) : ApiError(userMessage)
}
