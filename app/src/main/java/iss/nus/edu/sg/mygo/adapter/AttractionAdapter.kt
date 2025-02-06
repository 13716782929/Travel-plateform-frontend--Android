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

        holder.nameTextView.text = attraction.name
        holder.locationTextView.text = attraction.address
//        holder.priceTextView.text = attraction.price

        // 使用 Glide 加载图片 use Glide to load image
        if (attraction.imageUrls.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(attraction.imageUrls[0]) // 只加载第一张图片
                .placeholder(R.drawable.attraction_placeholder_image) // 加载中显示的占位图
                .error(R.drawable.attraction_placeholder_image) // 失败时显示的图片
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.attraction_placeholder_image) // 没有图片时使用默认占位图
        }

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
