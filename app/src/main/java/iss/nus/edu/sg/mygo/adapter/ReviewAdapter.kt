package iss.nus.edu.sg.mygo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.models.Review

/**
 * @ClassName ReviewAdapter
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/11
 * @Version 1.3
 */

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewUser: TextView = itemView.findViewById(R.id.review_user)
        val reviewText: TextView = itemView.findViewById(R.id.review_text)
        val reviewTimestamp: TextView = itemView.findViewById(R.id.review_timestamp)
        var reviewRatingBar: RatingBar = itemView.findViewById(R.id.review_rating_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        holder.reviewUser.text = "User ID: ${review.userId}" // 这里可以改成 username（如果后端提供）
        holder.reviewText.text = review.comment
        holder.reviewTimestamp.text = review.createdAt.toString() // 这里建议格式化时间
        holder.reviewRatingBar.rating = review.rating.toFloat()

        // 确保 `RatingBar` 正确显示星星
        holder.reviewRatingBar.numStars = 5
        holder.reviewRatingBar.stepSize = 0.5f
    }

    override fun getItemCount(): Int = reviews.size
}
