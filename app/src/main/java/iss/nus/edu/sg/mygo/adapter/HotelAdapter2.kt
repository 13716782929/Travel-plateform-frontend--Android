package iss.nus.edu.sg.mygo.adapter

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
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.Hotel
/**
 * @ClassName HotelAdapter2
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/9
 * @Version 1.3
 */
class HotelAdapter2(
    private var hotels: MutableList<Hotel>,
    private val onItemClick: (Int) -> Unit // 点击事件
) : RecyclerView.Adapter<HotelAdapter2.HotelViewHolder2>() {

    class HotelViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.text_hotel_name)
        val addressTextView: TextView = itemView.findViewById(R.id.text_address)
        val ratingTextView: TextView = itemView.findViewById(R.id.text_star_rating)
        val priceTextView: TextView = itemView.findViewById(R.id.text_price_per_night)
        val ImageView: ImageView = itemView.findViewById(R.id.container_mask_group)
        val hotelCard: RelativeLayout = itemView.findViewById(R.id.container_hotels_recomendation_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder2 {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hotel_card, parent, false)
        return HotelViewHolder2(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder2, position: Int) {
        val hotel = hotels[position]
        Log.d("HotelAdapter", "Binding hotel: ${hotel.name}, UUID: ${hotel.imageUrl}")

        holder.nameTextView.text = hotel.name
        holder.addressTextView.text = hotel.address
        holder.ratingTextView.text = hotel.rating
        holder.priceTextView.text = hotel.price

        //  直接使用 `/proxy/media/{uuid}` 访问后端图片
        val imageUrl = "http://10.0.2.2:8080/proxy/media/${hotel.imageUrl}?fileType=Small%20Thumbnail"


        // 使用 Glide 加载图片
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.hotel_image_rectangle) // 加载中的占位图
            .error(R.drawable.hotel_image_rectangle1) // 加载失败时的图片
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 启用缓存
            .into(holder.ImageView)

        // **添加点击事件，传递 `uuid` 到 `HotelDetailActivity.kt`**
        holder.hotelCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, HotelDetailActivity::class.java)
            intent.putExtra("hotel_uuid", hotel.uuid)
            holder.itemView.context.startActivity(intent) }

    }

    override fun getItemCount(): Int = hotels.size

    /**
     *  更新数据集并刷新 RecyclerView
     */
    fun updateData(newData: List<Hotel>) {
        hotels.clear()
        hotels.addAll(newData)
        notifyDataSetChanged()
    }

    /**
     * ✅ 安全获取 hotel 对象，避免 `IndexOutOfBoundsException`
     */
    fun getItem(position: Int): Hotel? {
        return if (position in hotels.indices) hotels[position] else null
    }

}
