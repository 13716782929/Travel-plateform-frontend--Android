package iss.nus.edu.sg.mygo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.AttractionItem

/***
 * author: Wang Chang & Siti Alifah Binte Yahya
 * StudentID:  & A0295324B
 * date: 25\1-25
 * */
class AttractionAdapter(
    private val attractions: List<Attraction>,
    private val onItemClick: (Int) -> Unit // 点击事件
) : RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder>() {

    inner class ViewHolder(private val binding: ItemAttractionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attraction: AttractionItem) {
            binding.attractionName.text = attraction.name
            binding.attractionLocation.text = attraction.address ?: "No Address Provided"

            // Load first image from the API
            attraction.images?.firstOrNull()?.url?.let { imageUrl ->
                Glide.with(binding.root.context).load(imageUrl).into(binding.attractionImage)
            }

            binding.root.setOnClickListener {
                onItemClick(attraction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attraction_card, parent, false)
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        Log.d("AttractionAdapter", "Binding attraction: ${attraction.attractionName}")
        holder.nameTextView.text = attraction.attractionName
        holder.locationTextView.text = attraction.location
        holder.imageView.setImageResource(attraction.imageResId)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return attractions.size
    }

    // ViewHolder用于绑定数据
    class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.attraction_name)
        val locationTextView: TextView = itemView.findViewById(R.id.attraction_location)
        val imageView: ImageView = itemView.findViewById(R.id.attraction_image)
    }
}
