package com.example.registernewuser

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.registernewuser.databinding.ActivityAttractionBinding

class AttractionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttractionBinding
    private lateinit var attractionsAdapter: AttractionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttractionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sample data
        val attractions = listOf(
            Attraction("Singapore Zoo", "80, Mandai Lake Rd, Singapore", "An unforgettable wildlife adventure at Mandai awaits!", 830, 10, Tourist, 49, R.drawable.singapore_zoo)
        )

        attractionsAdapter = AttractionsAdapter(attractions) { attraction ->
            val intent = Intent(this, AttractionDetailsActivity::class.java).apply {
                putExtra("attraction_name", attraction.name)
                putExtra("attraction_location", attraction.location)
                putExtra("attraction_rating", attraction.rating)
                putExtra("attraction_price", attraction.price)
                putExtra("attraction_image", attraction.imageResId)
            }
            startActivity(intent)
        }
        binding.recyclerView.adapter = attractionsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}