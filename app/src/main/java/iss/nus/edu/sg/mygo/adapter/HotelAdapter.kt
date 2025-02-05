package iss.nus.edu.sg.mygo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.models.Hotel

data class Hotel(
    val name: String,
    val address: String,
    val rating: String,
    val price: String,
    val imageResId: Int
)

class HotelAdapter(
    private val hotels: List<Hotel>
) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hotel_card, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotels[position]
        holder.nameTextView.text = hotel.name
        holder.addressTextView.text = hotel.address
        holder.ratingTextView.text = hotel.rating
        holder.priceTextView.text = hotel.price

        Log.d("HotelAdapter", "Image URL: ${hotel.imageUrl}")

        // 先检查是否为空，避免 Glide 传入 null
        val imageUrl = hotel.imageUrl ?: ""

        val glideUrl = GlideUrl(imageUrl, LazyHeaders.Builder()
            .addHeader("X-API-Key", "pRUJyzyyzpRd557ynCs7JRZtoKYr6PPC")
            .build())

        if (imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(glideUrl)
                .error(R.drawable.image_rectangle1) // 加载失败显示默认图
                .into(holder.ImageButton)
        } else if (hotel.imageResId != null) {
            holder.ImageButton.setImageResource(hotel.imageResId)
        } else {
            holder.ImageButton.setImageResource(R.drawable.image_rectangle1) // 再次兜底
        }

//        // 加载图片
//        Glide.with(holder.itemView.context)
//            .load(hotel.imageUrl)
//            .into(holder.ImageButton)
    }

    override fun getItemCount(): Int = hotels.size

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.text_hotel_name)
        val addressTextView: TextView = itemView.findViewById(R.id.text_address)
        val ratingTextView: TextView = itemView.findViewById(R.id.text_star_rating)
        val priceTextView: TextView = itemView.findViewById(R.id.text_price_per_night)
        val ImageButton: ImageButton = itemView.findViewById(R.id.container_mask_group)
    }
}
