package com.example.registernewuser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.registernewuser.databinding.ItemAttractionBinding
import com.example.registernewuser.models.Attraction

class AttractionsAdapter(
    private val attractions: List<Attraction>,
    private val onClick: (Attraction) -> Unit
) : RecyclerView.Adapter<AttractionsAdapter.ViewHolder>() {

    // ViewHolder for holding view references
    inner class ViewHolder(private val binding: ItemAttractionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attraction: Attraction) {
            binding.attractionName.text = attraction.name
            binding.attractionLocation.text = attraction.location
            binding.attractionPrice.text = "From $${attraction.price}/pax"
            binding.attractionImage.setImageResource(attraction.imageResId)

            // Set click listener
            binding.root.setOnClickListener {
                onClick(attraction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttractionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attractions[position])
    }

    override fun getItemCount(): Int = attractions.size
}
