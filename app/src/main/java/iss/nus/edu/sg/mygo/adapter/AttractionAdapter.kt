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
import iss.nus.edu.sg.mygo.databinding.ItemAttractionDetailsBinding
import iss.nus.edu.sg.mygo.models.AttractionImageResponse
import iss.nus.edu.sg.mygo.models.AttractionItem

/***
 * author: Wang Chang & Siti Alifah Binte Yahya
 * StudentID:  & A0295324B
 * date: 25 Jan 2025
 * */
class AttractionAdapter(

    private val attractions: List<AttractionItem>,
    private val onItemClick: (Int) -> Unit // 点击事件 Click listener for item clicks

) : RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder>() {
    class AttractionViewHolder {

    }


}
    class ViewHolder(private val binding: ItemAttractionDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attraction: AttractionItem) {
            binding.attractionName.text = attraction.name
            binding.attractionLocation.text = attraction.location
            binding.attractionPrice.text = "From $${attraction.ticketPrice}/pax"

            // Load first image from the back-end
            attraction.imageResId?.firstOrNull()?.url?.let { imageUrl ->
                Glide.with(binding.root.context).load(imageUrl).into(binding.attractionImage)
            }

            binding.root.setOnClickListener {
                onItemClick(attraction)
            }
        }
    // Create new views (invoked by the LayoutManager)
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attraction_card, parent, false)
        return AttractionViewHolder(view)
    }


    // Replace the contents of a view (invoked by the LayoutManager)
     fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        Log.d("AttractionAdapter", "Binding attraction: ${attraction.name}")
        holder.nameTextView.text = attraction.name
        holder.locationTextView.text = attraction.location.toString()
        holder.imageView.setImageResource(attraction.imageResId)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

     fun getItemCount(): Int {
        return attractions.size
    }

    // ViewHolder用于绑定数据
    class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.attraction_name)
        val locationTextView: TextView = itemView.findViewById(R.id.attraction_location)
        val imageView: ImageView = itemView.findViewById(R.id.attraction_image)
    }
}
