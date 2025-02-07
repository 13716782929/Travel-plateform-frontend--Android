package iss.nus.edu.sg.mygo.home
/*
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 7/2/25
 */
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import iss.nus.edu.sg.mygo.network.RetrofitClient
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.models.AttractionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttractionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttractionBinding
    private lateinit var attractionsAdapter: AttractionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttractionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        fetchAttractions()
    }

    private fun fetchAttractions() {
        val apiKey = "pnJuxp8vZLSQSLWGR0XYGUnA7PDRGPHr"
        RetrofitClient.apiService.getAttractions(apiKey).enqueue(object : Callback<AttractionResponse> {
            override fun onResponse(call: Call<AttractionResponse>, response: Response<AttractionResponse>) {
                if (response.isSuccessful) {
                    val attractions = response.body()?.data ?: emptyList()
                    attractionsAdapter = AttractionAdapter(attractions) { attraction ->
                        // Open AttractionDetailsActivity on click
                    }
                    binding.recyclerView.adapter = attractionsAdapter
                } else {
                    Log.e("API_ERROR", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AttractionResponse>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch data: ${t.message}")
            }
        })
    }
}