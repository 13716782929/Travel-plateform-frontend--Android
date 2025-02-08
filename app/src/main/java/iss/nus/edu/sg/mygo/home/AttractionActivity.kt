package iss.nus.edu.sg.mygo.home
/*
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 7 Feb 25
 */
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registernewuser.api.RetrofitClient
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

        val call = RetrofitClient.api.getAttractions(apiKey)
        call.enqueue(object : Callback<AttractionResponse> {
            override fun onResponse(
                call: Call<AttractionResponse>,
                response: Response<AttractionResponse>
            ) {
                if (response.isSuccessful) {
                    val attractions = response.body()?.data
                    attractions?.let {
                        // Use the attractions list in RecyclerView
                        Log.d("Attractions", it.toString())
                    }
                } else {
                    Log.e("API Error", response.errorBody()?.string().orEmpty())
                }
            }

            override fun onFailure(call: Call<AttractionResponse>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch data: ${t.message}")
            }
        })
    }
}