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

/**
 * @ClassName BookingAdapter
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/10
 * @Version 1.3
 */

class BookingAdapter(
    private var bookings: List<BookingItem>,
    private val onDeleteClick: (BookingItem) -> Unit,
    private val onCommentClick: (BookingItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ATTRACTION = 1
        private const val TYPE_HOTEL = 2
        private const val TYPE_FLIGHT = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (bookings[position]) {
            is BookingItem.AttractionBookingItem -> TYPE_ATTRACTION
            is BookingItem.HotelBookingItem -> TYPE_HOTEL
//            is BookingItem.FlightBookingItem -> TYPE_FLIGHT
            else -> throw IllegalArgumentException("Invalid booking type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ATTRACTION -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_attraction_booking, parent, false)
                // Debug 日志，检查 View 是否为空
                Log.d("BookingAdapter", "Created ViewHolder for Attraction Booking")

                AttractionViewHolder(view)
            }
            TYPE_HOTEL -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel_booking, parent, false)
                HotelViewHolder(view)
            }
//            TYPE_FLIGHT -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flight_booking, parent, false)
//                FlightViewHolder(view)
//            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val bookingItem = bookings[position]) {
            is BookingItem.AttractionBookingItem -> (holder as AttractionViewHolder).bind(bookingItem, onDeleteClick, onCommentClick)
            is BookingItem.HotelBookingItem -> (holder as HotelViewHolder).bind(bookingItem, onDeleteClick, onCommentClick)
            else -> {}
        }
    }

    override fun getItemCount(): Int = bookings.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateBookings(newBookings: List<BookingItem>) {
        bookings = newBookings
        notifyDataSetChanged()
    }

    /**
     * Hotel 视图展示
     */
    class HotelViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val hotelName: TextView = itemView.findViewById(R.id.item_hotel_booking_name)
        private val location: TextView = itemView.findViewById(R.id.item_hotel_booking_location)
        private val checkInDate: TextView = itemView.findViewById(R.id.item_hotel_booking_check_in_date)
        private val checkOutDate: TextView = itemView.findViewById(R.id.item_hotel_booking_check_out_date)
        private val roomType: TextView = itemView.findViewById(R.id.item_hotel_booking_room_type)
        private val guestNumber: TextView = itemView.findViewById(R.id.item_hotel_booking_guest)
        private val hotelImage: ImageView = itemView.findViewById(R.id.hotel_booking_container_mask_group)
        private val deleteButton: Button = itemView.findViewById(R.id.btn_delete_hotel_booking)
        private val commentButton: Button = itemView.findViewById(R.id.btn_review_hotel_booking)

        fun bind(
            bookingItem: BookingItem.HotelBookingItem,
            onDeleteClick: (BookingItem) -> Unit,
            onCommentClick: (BookingItem) -> Unit
        ) {
            hotelName.text = bookingItem.hotelBooking.hotelName
            location.text = "Location: ${bookingItem.hotelBooking.location}"
            checkInDate.text = "Check-in: ${bookingItem.hotelBooking.checkInDate}"
            checkOutDate.text = "Check-out: ${bookingItem.hotelBooking.checkOutDate}"
            roomType.text = "RoomType: ${bookingItem.hotelBooking.roomType}"
            guestNumber.text = "Guests: ${bookingItem.hotelBooking.guests}"

            val imageUrl = "http://10.0.2.2:8080/proxy/media/${bookingItem.hotelBooking.hotelImageUuid}"
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.attraction_placeholder_image)
                .error(R.drawable.attraction_placeholder_image)
                .into(hotelImage)

            deleteButton.setOnClickListener { onDeleteClick(bookingItem) }
            commentButton.setOnClickListener { onCommentClick(bookingItem) }
        }
    }

    /**
     * Attraction 视图展示
     */
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