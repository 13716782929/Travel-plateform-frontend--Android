package com.example.registernewuser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registernewuser.databinding.ActivityAttractionBinding
import com.example.registernewuser.models.Attraction

class AttractionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttractionBinding
    private lateinit var attractionsAdapter: AttractionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttractionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sample data
        val attractions = listOf(
            Attraction(
                "Singapore Zoo",
                "80 Mandai Lake Rd",
                "Amazing wildlife adventure awaits!",
                830,
                10,
                "Tourist",
                49,
                R.drawable.singapore_zoo),
        )

        // Set up RecyclerView with Adapter
        attractionsAdapter = AttractionsAdapter(attractions) { attraction ->
            // Navigate to AttractionDetailsFragment or AttractionDetailsActivity
            val fragment = AttractionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("attraction_name", attraction.name)
                    putString("attraction_location", attraction.location)
                    putString("attraction_description", attraction.description)
                    putInt("attraction_openTime", attraction.openTime)
                    putInt("attraction_ticketAvailability", attraction.ticketAvailability)
                    putString("attraction_ticketType", attraction.ticketType)
                    putInt("attraction_price", attraction.price)
                    putInt("attraction_image", attraction.imageResId)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // Attach adapter to RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = attractionsAdapter
    }
}