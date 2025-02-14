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

/**
 * @ClassName AttractionAdapter
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/25
 * @Version 1.3
 */
class AttractionAdapter(
    private var attractions: MutableList<Attraction>,
    private val onItemClick: (Int) -> Unit // 点击事件
) : RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder>() {

    /**
     * ViewHolder 绑定 UI 组件
     */
    class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.attraction_name)
        val locationTextView: TextView = itemView.findViewById(R.id.attraction_location)
        val imageView: ImageView = itemView.findViewById(R.id.attraction_image)
        val rateTextView: TextView = itemView.findViewById(R.id.attraction_rate)
        val pricingTextView: TextView = itemView.findViewById(R.id.attraction_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attraction_card, parent, false)
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        Log.d("AttractionAdapter", "Binding attraction: ${attraction.name}, UUID: ${attraction.imageUuid}")

        holder.nameTextView.text = attraction.name
        holder.locationTextView.text = attraction.address
        holder.rateTextView.text = String.format("%.1f", attraction.rate) // 保留 1 位小数
        holder.pricingTextView.text = attraction.price

        //  直接使用 `/proxy/media/{uuid}` 访问后端图片
        val imageUrl = "http://10.0.2.2:8080/proxy/media/${attraction.imageUuid}?fileType=Small%20Thumbnail"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.attraction_placeholder_image) // 加载中占位图
            .error(R.drawable.attraction_placeholder_image) // 加载失败默认图片
            .into(holder.imageView)

        // 绑定点击事件
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount(): Int = attractions.size

    /**
     *  更新数据集并刷新 RecyclerView
     */
    fun updateData(newData: List<Attraction>) {
        attractions.clear()
        attractions.addAll(newData)
        notifyDataSetChanged()
    }

    /**
     * 安全获取 Attraction 对象，避免 `IndexOutOfBoundsException`
     */
    fun getItem(position: Int): Attraction? {
        return if (position in attractions.indices) attractions[position] else null
    }



    /**
     * get Attraction list
     */
    fun getItemList(): List<Attraction> {
        return attractions
    }
}
