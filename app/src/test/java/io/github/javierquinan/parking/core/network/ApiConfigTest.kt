package io.github.javierquinan.parking.core.network

import org.junit.Assert.assertTrue
import org.junit.Test

class ApiConfigTest {
    @Test
    fun debugEndpointResolvesParkingResource() {
        val endpoint = ApiConfig.endpointOrNull()
        assertTrue(endpoint?.endsWith("/auto.php") == true)
    }
}
