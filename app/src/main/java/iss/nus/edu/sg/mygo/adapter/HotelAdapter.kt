package iss.nus.edu.sg.mygo.adapter
/**
 * @Description
 * @Author YAO YIYANG
 * @StudentID A0294873L
 * @Date 2025/2/11
 * @Version 1.0
 */


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.home.HotelDetailActivity
import iss.nus.edu.sg.mygo.models.Hotel

class HotelAdapter(
    private val hotels: List<Hotel>
) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.text_hotel_name)
        val addressTextView: TextView = itemView.findViewById(R.id.text_address)
        val ratingTextView: TextView = itemView.findViewById(R.id.text_star_rating)
        val priceTextView: TextView = itemView.findViewById(R.id.text_price_per_night)
        val ImageView: ImageView = itemView.findViewById(R.id.container_mask_group)
        val hotelCard: RelativeLayout = itemView.findViewById(R.id.container_hotels_recomendation_card)
    }

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

        Log.d("IMAGE_URL", "Image URL: " + hotel.imageUrl);


        // 使用 Glide 加载图片
        Glide.with(holder.itemView.context)
            .load(hotel.imageUrl)
            .placeholder(R.drawable.hotel_image_rectangle) // 加载中的占位图
            .error(R.drawable.hotel_image_rectangle1) // 加载失败时的图片
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 启用缓存
            .into(holder.ImageView)

        // **添加点击事件，传递 `uuid` 到 `HotelDetailActivity.kt`**
        holder.hotelCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, HotelDetailActivity::class.java)
            intent.putExtra("hotel_uuid", hotel.uuid)
            holder.itemView.context.startActivity(intent)
        }



    }

    override fun getItemCount(): Int = hotels.size


}
