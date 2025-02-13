package com.example.parcial

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


class MainActivity : AppCompatActivity() {
    val codigos = ArrayList<String>()

    lateinit var txtplaca: EditText
    lateinit var txtmodelo: EditText
    lateinit var txtanio: EditText
    lateinit var txtcolor: EditText
    lateinit var txtfecha: EditText
    lateinit var txthoraentrada: EditText
    lateinit var txthorasalida: EditText
    lateinit var txtcodigo: EditText


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtplaca = findViewById<EditText>(R.id.txt_placa)
        txtmodelo = findViewById<EditText>(R.id.txt_modelo)
        txtanio = findViewById<EditText>(R.id.txt_año)
        txtcolor = findViewById<EditText>(R.id.txt_color)
        txtfecha = findViewById<EditText>(R.id.txt_fecha)
        txthoraentrada = findViewById<EditText>(R.id.txt_entrada)
        txthorasalida = findViewById<EditText>(R.id.txt_salida)
        txtcodigo = findViewById(R.id.txt_dato)

        val lista = findViewById<ListView>(R.id.lista)
        lista.setOnItemClickListener{adapterView, view, i, l ->
            val elementoSeleccionado = adapterView.getItemAtPosition(i).toString()

            Log.d("elemento seleccionado", elementoSeleccionado)
            Log.d("elemento seleccionado IIII", codigos.toString())
            consultarDatos(codigos[i])
        }

        val btnconsultar = findViewById<Button>(R.id.btn_consultar)
        btnconsultar.setOnClickListener {
            consultar(lista)
        }

        val btningresar = findViewById<Button>(R.id.btn_ingresar)
        btningresar.setOnClickListener {
            insertar(txtplaca.text.toString(), txtmodelo.text.toString(), txtanio.text.toString(), txtcolor.text.toString(), txtfecha.text.toString(), txthoraentrada.text.toString(), txthorasalida.text.toString() )
        }

        val btnmodificar = findViewById<Button>(R.id.btn_modificar)
        btnmodificar.setOnClickListener {
            actualizar()
        }



    }

    private fun insertar(placa: String, modelo: String, anio: String, color: String, fecha: String, entrada: String, salida: String){
        val url = "http://10.0.2.2/Parcial/auto.php"
        val datos = JSONObject()

        datos.put("accion", "Insertar")
        datos.put("placa", placa)
        datos.put("modelo", modelo)
        datos.put("anio", anio)
        datos.put("color", color)
        datos.put("fecha", fecha)
        datos.put("entrada", entrada)
        datos.put("salida", salida)

        val rq = Volley.newRequestQueue(this)
        val jsor = JsonObjectRequest(com.android.volley.Request.Method.POST, url, datos,
            { s ->
                try {
                    val obj = (s)
                    if(obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, obj.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }
        )
        rq.add(jsor)
    }

    private fun consultar(lista:ListView){
        val al = ArrayList<String>()
        al.clear()
        val url = "http://10.0.2.2/Parcial/auto.php"
        val datos = JSONObject()

        datos.put("accion", "consultar")
        codigos.clear()
        val rq = Volley.newRequestQueue(this)
        val jsor = JsonObjectRequest(
            com.android.volley.Request.Method.POST, url, datos,
            { s ->
                try {
                    val obj = (s)
                    Toast.makeText(applicationContext, obj.getBoolean("estado").toString(), Toast.LENGTH_LONG).show()

                    if(obj.getBoolean("estado")){
                        val array = obj.getJSONArray("autos")

                        for (i in 0 until array.length()){
                            val fila = array.getJSONObject(i)
                            codigos.add(fila.getString("codigo"))
                            al.add(fila.getString("placa") + " " +fila.getString("modelo") + " " + fila.getString("fecha") + " " + fila.getString("entrada"))
                        }

                        val la = ArrayAdapter(this, android.R.layout.simple_list_item_1, al)
                        lista.adapter = la
                    }else {
                        Toast.makeText(applicationContext, obj.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_SHORT).show() }
        )
        rq.add(jsor)
    }

    private fun consultarDatos(codigo: String){
        val url = "http://10.0.2.2/Parcial/auto.php"
        val datos = JSONObject()
        datos.put("accion", "Datos")
        datos.put("codigo", codigo)

        val rq = Volley.newRequestQueue(this)
        val jsor = JsonObjectRequest(
            com.android.volley.Request.Method.POST, url, datos,
            { s ->
                try {
                    val obj = (s)

                    if(obj.getBoolean("estado")){
                        val array = obj.getJSONArray("auto")
                        val dato = array.getJSONObject(0)

                        txtcodigo.setText((dato.getString("codigo")))
                        txtplaca.setText(dato.getString("placa"))
                        txtmodelo.setText(dato.getString("modelo"))
                        txtanio.setText(dato.getString("anio"))
                        txtcolor.setText(dato.getString("color"))
                        txtfecha.setText(dato.getString("fecha"))
                        txthoraentrada.setText(dato.getString("entrada"))
                        txthorasalida.setText(dato.getString("salida"))
                    } else {
                        Toast.makeText(applicationContext, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
                }
            },
            { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }
        )
        rq.add(jsor)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizar(){
        val placa = txtplaca.text.toString()
        val modelo = txtmodelo.text.toString()
        val anio = txtanio.text.toString()
        val color = txtcolor.text.toString()
        val fecha = txtfecha.text.toString()
        val entradaString = txthoraentrada.text.toString()
        val salidaString = txthorasalida.text.toString()
        val codigo = txtcodigo.text.toString()
        val estado = 0

        val horaEntrada = convertirHora(entradaString)
        val horaSalida = convertirHora(salidaString)
        val tarifaPorHora = 1.0
        val tarifaTotal = calcularTarifaTotal(horaEntrada, horaSalida, tarifaPorHora, placa)


        val url = "http://10.0.2.2/Parcial/auto.php"
        val datos = JSONObject()

        datos.put("accion", "Actualizar")
        datos.put("placa", placa)
        datos.put("modelo", modelo)
        datos.put("anio", anio)
        datos.put("color", color)
        datos.put("fecha", fecha)
        datos.put("entrada", entradaString)
        datos.put("salida", salidaString)
        datos.put("codigo", codigo)
        datos.put("tarifa_total", tarifaTotal)
        datos.put("estado", estado)

        val rq = Volley.newRequestQueue(this)
        val jsor = JsonObjectRequest(
            com.android.volley.Request.Method.POST, url, datos,
            { s ->
                try {
                    val obj = (s)
                    if(obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, obj.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e:JSONException){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }
        )
        rq.add(jsor)
        val intent = Intent(this, MainActivity2::class.java)
        intent.putExtra("TARIFA_TOTAL", tarifaTotal)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calcularTarifaTotal(entrada: LocalTime, salida: LocalTime, tarifaPorHora: Double, placa: String): Double {
        val tiempoTranscurrido = ChronoUnit.HOURS.between(entrada, salida)
        var tarifaBase = tiempoTranscurrido * tarifaPorHora
        val tarifaTotal = tarifaBase

        return tarifaTotal
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertirHora(hora: String): LocalTime {
        val horaFormateada = if (hora.endsWith("AM") || hora.endsWith("PM")) {
            val formato24Horas = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val hora24Horas = SimpleDateFormat("HH:mm", Locale.getDefault())
            val horaDate = formato24Horas.parse(hora)
            hora24Horas.format(horaDate)
        } else {
            hora
        }

        return LocalTime.parse(horaFormateada)
    }


}