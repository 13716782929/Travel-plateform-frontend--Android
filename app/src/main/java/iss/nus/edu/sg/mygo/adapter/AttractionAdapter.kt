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

/***
 * author: Wang Chang
 * date: 25\1-25
 * */
class AttractionAdapter(
    private val attractions: List<Attraction>,
    private val onItemClick: (Int) -> Unit // 点击事件
) : RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attraction_card, parent, false)
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        Log.d("AttractionAdapter", "Binding attraction: ${attraction.name}")
        Log.d("AttractionAdapter", "Binding attraction: ${attraction.imageUuid}")

        holder.nameTextView.text = attraction.name
        holder.locationTextView.text = attraction.address
//        holder.priceTextView.text = attraction.price

        // ✅ 直接使用 `/proxy/media/{uuid}` 访问后端二进制数据
        val imageUrl = "http://10.0.2.2:8080/proxy/media/${attraction.imageUuid}?fileType=Small%20Thumbnail"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.attraction_placeholder_image) // 占位图
            .error(R.drawable.attraction_placeholder_image) // 加载失败的默认图片
            .into(holder.imageView)

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
//        val priceTextView: TextView = itemView.findViewById(R.id.pax)
        val imageView: ImageView = itemView.findViewById(R.id.attraction_image)
    }

}
