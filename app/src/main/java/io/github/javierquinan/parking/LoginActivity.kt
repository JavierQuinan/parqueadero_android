package io.github.javierquinan.parking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.github.javierquinan.parking.core.network.ApiConfig
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
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
        val url = ApiConfig.endpointOrNull() ?: run {
            Toast.makeText(
                this,
                "El endpoint de la API no está configurado.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val payload = JSONObject().apply {
            put("accion", "consultarDato")
            put("usuario", username)
            put("clave", password)
        }

        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            payload,
            { response ->
                try {
                    val authenticated = response.getString("estado").toInt() == 1
                    if (authenticated) {
                        resetFailedAttempts()
                        val intent = Intent(this, ParkingManagementActivity::class.java).apply {
                            putExtra("idPersona", response.getInt("cod_persona"))
                        }
                        startActivity(intent)
                        finish()
                    } else {
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
                } catch (exception: JSONException) {
                    Toast.makeText(
                        applicationContext,
                        "Error en los datos recibidos.",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("LoginError", "JSONException: ${exception.message}")
                }
            },
            { error ->
                Toast.makeText(
                    applicationContext,
                    "Error en la solicitud: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("LoginError", "VolleyError: ${error.message}")
            }
        )

        requestQueue.add(request)
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
