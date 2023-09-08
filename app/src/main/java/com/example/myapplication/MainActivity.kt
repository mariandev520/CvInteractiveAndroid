package com.example.myapplication


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AlertDialog

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {


    private lateinit var userNameTextView: TextView
    private lateinit var userLocationTextView: TextView
    private lateinit var userPhoneTextView: TextView
    private lateinit var userEmailTextView: TextView
    private  lateinit var  userAvatarImageView : ImageView


    private var cvNuevo = ""
    private var booleano = false

    private lateinit var cambiarCvButton: Button
    private lateinit var openChatButton: Button
    private lateinit var enviarFormularioButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas


        fun parseJson(response: String): JSONObject {
            return JSONObject(response)
        }

        userAvatarImageView = findViewById(R.id.userAvatarImageView)
        userNameTextView = findViewById(R.id.userNameTextView)
        userNameTextView = findViewById(R.id.userNameTextView)
        userPhoneTextView = findViewById(R.id.userPhoneTextView)
        userLocationTextView = findViewById(R.id.userLocationTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)

        cambiarCvButton = findViewById(R.id.cambiarCvButton)
        openChatButton = findViewById(R.id.openChatButton)
        enviarFormularioButton = findViewById(R.id.enviarFormularioButton)

        // Configurar botones
        cambiarCvButton.setOnClickListener {
            cambiarValor()
            fetchDataFromApi()
        }

        openChatButton.setOnClickListener {
            // Lógica para abrir el chat de WhatsApp
        }

        enviarFormularioButton.setOnClickListener {
            // Lógica para enviar el formulario
        }

        // Asignar un valor inicial a cvNuevo
        cambiarValor()

        // Inicializar la vista de usuario
        fetchDataFromApi()
    }

    private fun cambiarValor() {
        booleano = !booleano
        cvNuevo = if (booleano) "https://randomuser.me/api/" else ""
    }


    fun makeHttpRequest(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val inputStream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        inputStream.close()
        return response.toString()
    }


    fun fetchDataFromApi() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = "https://randomuser.me/api/"
                val response = makeHttpRequest(url)

                val jsonResponse = JSONObject(response)
                val user = jsonResponse.getJSONArray("results").getJSONObject(0)

                // Obtener la URL de la imagen del usuario
                val userImageUrl = user.getJSONObject("picture").getString("large")

                // Cargar la imagen del usuario en userAvatarImageView usando Picasso
                runOnUiThread {
                    Picasso.get()
                        .load(userImageUrl)
                        .into(userAvatarImageView)

                    // Resto del código para actualizar los TextViews
                    val userName = "${user.getJSONObject("name").getString("first")} ${user.getJSONObject("name").getString("last")}"
                    val userEmail = user.getString("email")
                    val userPhone = user.getString("phone")
                    val userLocation = "${user.getJSONObject("location").getString("city")}, ${user.getJSONObject("location").getString("country")}"

                    userNameTextView.text = userName
                    userEmailTextView.text = userEmail
                    userPhoneTextView.text = userPhone
                    userLocationTextView.text = userLocation
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
