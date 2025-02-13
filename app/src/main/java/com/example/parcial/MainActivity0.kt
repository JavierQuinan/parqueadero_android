package com.example.parcial

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
import org.json.JSONException
import org.json.JSONObject

class MainActivity0 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main0)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val usuario = findViewById<EditText>(R.id.txtUsuario).text.toString()
            val clave = findViewById<EditText>(R.id.txtClave).text.toString()
            login(usuario, clave)
        }
    }
    private fun login(usuario: String, clave: String) {
        val url = "http://10.0.2.2/Parcial/auto.php" // Usa la IP correcta de tu máquina local

        // Crear los datos que serán enviados en el cuerpo de la solicitud
        val datos = JSONObject()
        datos.put("accion", "consultarDato")
        datos.put("usuario", usuario) // Usando los valores que se pasan como parámetro
        datos.put("clave", clave)   // Usando los valores que se pasan como parámetro

        // Crear la cola de peticiones
        val rq = Volley.newRequestQueue(this)

        // Crear la solicitud JsonObjectRequest
        val jsor = JsonObjectRequest(
            Request.Method.POST, url, datos,
            { response ->
                try {
                    // Analizar la respuesta del servidor
                    val obj = response

                    // Convertir el valor de "estado" de 1/0 a un booleano
                    val estado = obj.getString("estado").toInt() == 1

                    // Verificar si la respuesta contiene un estado positivo
                    if (estado) {
                        reiniciarIntentosFallidos()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("idPersona", obj.getInt("cod_persona"))
                        startActivity(intent)
                        finish()
                    } else {
                        incrementarIntentosFallidos() // Incrementar los intentos fallidos
                        val intentos = obtenerIntentosFallidos()

                        if (intentos >= MAX_INTENTOS) {
                            Toast.makeText(
                                applicationContext,
                                "Cuenta bloqueada por múltiples intentos fallidos. Contacta al administrador.",
                                Toast.LENGTH_LONG
                            ).show()
                            bloquearBotonLogin()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Intento fallido $intentos de $MAX_INTENTOS. Verifica tus credenciales.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: JSONException) {
                    // Manejar posibles excepciones durante el parseo del JSON
                    Toast.makeText(
                        applicationContext,
                        "Error en los datos recibidos",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("LoginError", "JSONException: ${e.message}")
                }
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(
                    applicationContext,
                    "Error en la solicitud: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("LoginError", "VolleyError: ${error.message}")
            }
        )

        // Agregar la solicitud a la cola
        rq.add(jsor)
    }

    private val MAX_INTENTOS = 3

    private fun incrementarIntentosFallidos() {
        val prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val intentos = prefs.getInt("intentos_fallidos", 0) + 1
        editor.putInt("intentos_fallidos", intentos)
        editor.apply()
    }

    private fun obtenerIntentosFallidos(): Int {
        val prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        return prefs.getInt("intentos_fallidos", 0)
    }

    private fun reiniciarIntentosFallidos() {
        val prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("intentos_fallidos", 0)
        editor.apply()
    }

    private fun bloquearBotonLogin() {
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            btnLogin.isEnabled = true
        }, 30000) // Bloquear por 30 segundos
    }

}