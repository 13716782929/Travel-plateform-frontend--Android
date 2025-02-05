package com.example.registernewuser

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.registernewuser.databinding.ActivityAttractionDetailsBinding

class AttractionDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttractionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttractionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
br
        // Get data from Intent
        val name = intent.getStringExtra("name")
        val location = intent.getStringExtra("location")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val price = intent.getStringExtra("price")
        val image = intent.getIntExtra("image", 0)

        // Set data to views
        binding.attractionName.text = name
        binding.attractionDescription.text = location
        binding.attractionImage.setImageResource(image)
    }
}
