package io.github.javierquinan.parking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.github.javierquinan.parking.core.network.ApiResult
import io.github.javierquinan.parking.data.remote.LegacyParkingApiClient
import io.github.javierquinan.parking.data.remote.model.LoginRequest

class LoginActivity : AppCompatActivity() {
    private val apiClient by lazy { LegacyParkingApiClient(this) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val username = findViewById<EditText>(R.id.txtUsuario).text.toString().trim()
            val password = findViewById<EditText>(R.id.txtClave).text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Ingresa usuario y contraseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        apiClient.login(LoginRequest(username, password)) { result ->
            when (result) {
                is ApiResult.Success -> {
                    if (result.value.authenticated) {
                        resetFailedAttempts()
                        val personId = result.value.personId
                        if (personId == null) {
                            Toast.makeText(
                                this,
                                "La respuesta de autenticación no contiene el identificador esperado.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@login
                        }

                        val intent = Intent(this, ParkingManagementActivity::class.java).apply {
                            putExtra("idPersona", personId)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        handleRejectedLogin()
                    }
                }

                is ApiResult.Failure -> {
                    Toast.makeText(this, result.error.userMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleRejectedLogin() {
        incrementFailedAttempts()
        val attempts = getFailedAttempts()

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            Toast.makeText(
                applicationContext,
                "Cuenta bloqueada por múltiples intentos fallidos. Contacta al administrador.",
                Toast.LENGTH_LONG
            ).show()
            temporarilyDisableLogin()
        } else {
            Toast.makeText(
                applicationContext,
                "Intento fallido $attempts de $MAX_FAILED_ATTEMPTS. Verifica tus credenciales.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun incrementFailedAttempts() {
        val preferences = getSharedPreferences(LOGIN_PREFERENCES, MODE_PRIVATE)
        val attempts = preferences.getInt(FAILED_ATTEMPTS_KEY, 0) + 1
        preferences.edit().putInt(FAILED_ATTEMPTS_KEY, attempts).apply()
    }

    private fun getFailedAttempts(): Int =
        getSharedPreferences(LOGIN_PREFERENCES, MODE_PRIVATE)
            .getInt(FAILED_ATTEMPTS_KEY, 0)

    private fun resetFailedAttempts() {
        getSharedPreferences(LOGIN_PREFERENCES, MODE_PRIVATE)
            .edit()
            .putInt(FAILED_ATTEMPTS_KEY, 0)
            .apply()
    }

    private fun temporarilyDisableLogin() {
        val loginButton = findViewById<Button>(R.id.btnLogin)
        loginButton.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed(
            { loginButton.isEnabled = true },
            LOGIN_LOCKOUT_MILLIS
        )
    }

    private companion object {
        const val MAX_FAILED_ATTEMPTS = 3
        const val LOGIN_LOCKOUT_MILLIS = 30_000L
        const val LOGIN_PREFERENCES = "LoginPrefs"
        const val FAILED_ATTEMPTS_KEY = "intentos_fallidos"
    }
}
