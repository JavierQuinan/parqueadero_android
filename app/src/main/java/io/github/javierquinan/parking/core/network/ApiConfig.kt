package io.github.javierquinan.parking.core.network

import io.github.javierquinan.parking.BuildConfig

object ApiConfig {
    private const val PARKING_RESOURCE = "auto.php"

    fun endpointOrNull(): String? {
        val baseUrl = BuildConfig.API_BASE_URL.trim()
        if (baseUrl.isEmpty()) return null

        return "${baseUrl.trimEnd('/')}/$PARKING_RESOURCE"
    }
}
