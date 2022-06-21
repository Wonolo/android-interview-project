package com.wonolo.interview

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class MainActivity : AppCompatActivity() {
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private val pokemonListAdapter = PokemonListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
        loadCharacters()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.pokemonList)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = pokemonListAdapter
        loadingIndicator = findViewById(R.id.loadingIndicator)
    }

    private fun loadCharacters() {
        loadingIndicator.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val response = makeRequest()
            withContext(Dispatchers.Main) {
                loadingIndicator.visibility = View.GONE
                try {
                    Moshi.Builder().build().adapter(PokemonResponseModel::class.java)
                        .fromJson(response)?.let { data ->
                            pokemonListAdapter.data = data.results
                        }
                } catch (e: Exception) {
                    showError()
                }
            }
        }
    }

    private fun showError() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Something went wrong")
            .setPositiveButton("Ok") { _, _ -> }
    }

    private suspend fun makeRequest(): String {
        return suspendCancellableCoroutine { continuation ->
            try {
                val reader: BufferedReader
                val url = URL("https://pokeapi.co/api/v2/pokemon/?offset=0&limit=50")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    reader = BufferedReader(InputStreamReader(inputStream) as Reader?)

                    val response = StringBuffer()
                    var inputLine = reader.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = reader.readLine()
                    }
                    reader.close()

                    if (continuation.isActive) {
                        continuation.resume(response.toString())
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (continuation.isActive) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }
}