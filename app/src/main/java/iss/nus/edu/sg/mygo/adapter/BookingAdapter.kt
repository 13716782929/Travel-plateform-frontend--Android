package iss.nus.edu.sg.mygo.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.models.BookingItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import iss.nus.edu.sg.mygo.R
import java.text.SimpleDateFormat
import java.util.Locale


class BookingAdapter(
    private var bookings: List<BookingItem>,
    private val onDeleteClick: (BookingItem.AttractionBookingItem) -> Unit,
    private val onCommentClick: (BookingItem.AttractionBookingItem) -> Unit
) : RecyclerView.Adapter<BookingAdapter.AttractionViewHolder>() {

    companion object {
        private const val TYPE_ATTRACTION = 1
        private const val TYPE_HOTEL = 2
        private const val TYPE_FLIGHT = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (bookings[position]) {
            is BookingItem.AttractionBookingItem -> TYPE_ATTRACTION
            is BookingItem.HotelBookingItem -> TYPE_HOTEL
            is BookingItem.FlightBookingItem -> TYPE_FLIGHT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        return when (viewType) {
            TYPE_ATTRACTION -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_attraction_booking, parent, false)
                // Debug 日志，检查 View 是否为空
                Log.d("BookingAdapter", "Created ViewHolder for Attraction Booking")

                AttractionViewHolder(view)
            }
//            TYPE_HOTEL -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel_booking, parent, false)
//                HotelViewHolder(view)
//            }
//            TYPE_FLIGHT -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flight_booking, parent, false)
//                FlightViewHolder(view)
//            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val bookingItem = bookings[position] as BookingItem.AttractionBookingItem
        holder.bind(bookingItem, onDeleteClick, onCommentClick)
    }

    override fun getItemCount(): Int = bookings.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateBookings(newBookings: List<BookingItem>) {
        bookings = newBookings
        notifyDataSetChanged()
    }

    class AttractionViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val deleteButton: Button = itemView.findViewById(R.id.btn_delete_attraction_booking)
        private val commentButton: Button =
            itemView.findViewById(R.id.btn_review_attraction_booking)
        private val attractionName: TextView = itemView.findViewById(R.id.text_attraction_name)
        private val visitDate: TextView =
            itemView.findViewById(R.id.item_attraction_booking_visit_date)
        private val visitTime: TextView =
            itemView.findViewById(R.id.item_attraction_booking_visit_time)
        private val location: TextView = itemView.findViewById(R.id.text_location)
        private val attractionImage: ImageView = itemView.findViewById(R.id.attraction_booking_container_mask_group)

        fun bind(
            bookingItem: BookingItem.AttractionBookingItem,
            onDeleteClick: (BookingItem.AttractionBookingItem) -> Unit,
            onCommentClick: (BookingItem.AttractionBookingItem) -> Unit
        ) {
            attractionName.text = bookingItem.attractionBooking.attractionName
            visitDate.text = "Date: ${bookingItem.attractionBooking.visitDate}"
            visitTime.text = "Time: ${formatVisitTime(bookingItem.attractionBooking.visitTime)}"
            location.text = bookingItem.attractionBooking.location

            // 加载图片
            val imageUuid = bookingItem.attractionBooking.attractionImageUuid
            val imageUrl = "http://10.0.2.2:8080/proxy/media/${imageUuid}?fileType=Small%20Thumbnail"

            Log.d("BookingAdapter", "Loading image for attractionUuid: $imageUuid")
            Log.d("BookingAdapter", "Constructed imageUrl: $imageUrl")

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.attraction_placeholder_image)
                .error(R.drawable.attraction_placeholder_image)
                .into(attractionImage)

            deleteButton.setOnClickListener {
                onDeleteClick(bookingItem)
                Toast.makeText(
                    itemView.context,
                    "Delete ${bookingItem.attractionBooking.attractionName}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            commentButton.setOnClickListener {
                onCommentClick(bookingItem)
                Toast.makeText(
                    itemView.context,
                    "Comment on ${bookingItem.attractionBooking.attractionName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /**
         * 处理 visit_time 只显示 `HH:mm` 格式
         */
        private fun formatVisitTime(visitTime: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // ✅ 修正格式
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = inputFormat.parse(visitTime)
                outputFormat.format(date ?: return visitTime) // 如果解析失败，返回原始字符串
            } catch (e: Exception) {
                visitTime // 解析失败，返回原始时间
            }
        }
    }
}