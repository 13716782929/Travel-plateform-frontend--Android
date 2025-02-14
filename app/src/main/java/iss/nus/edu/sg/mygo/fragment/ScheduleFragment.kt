package iss.nus.edu.sg.mygo.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.BookingAdapter
import iss.nus.edu.sg.mygo.api.models.AttractionData
import iss.nus.edu.sg.mygo.api.models.ReviewRequest
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import iss.nus.edu.sg.mygo.api.service.FlightApiService
import iss.nus.edu.sg.mygo.api.service.HotelApiService
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.enum.RoomType
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.AttractionBooking
import iss.nus.edu.sg.mygo.models.Booking
import iss.nus.edu.sg.mygo.models.BookingItem
import iss.nus.edu.sg.mygo.models.Hotel
import iss.nus.edu.sg.mygo.models.HotelBooking
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.math.BigDecimal

/**
 * @ClassName ScheduleFragment
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/10
 * @Version 1.3
 */

class ScheduleFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter

    private val apiService = UserApiService.create()
    private val sessionManager by lazy { SessionManager(requireContext()) }
    private val attractionApiService = AttractionApiService.create()
    private val hotelApiService = HotelApiService.create()
    private val flightApiService = FlightApiService.create()



    private var selectedDate: String = ""
    private var allBookings: List<BookingItem> = emptyList() // 用户所有booking的信息
    private var filteredBookings: List<BookingItem> = emptyList() // 日期筛选后的bookings
    // Attraction 缓存，避免重复请求

    private val attractionCache = mutableMapOf<String, Attraction>()
    private val hotelCache = mutableMapOf<String, Hotel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val isLoggedIn = sessionManager.isLoggedIn()
        showToast("🔍 DEBUG: isLoggedIn = $isLoggedIn") // ✅ 打印登录状态

        if (!isLoggedIn) {
            showToast("🔍 DEBUG: User not logged in, redirecting to LoginActivity")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return null
        }

        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewBookings)
        setupRecyclerView()

        val userId = sessionManager.getUserIdFromPrefs()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
        } else {
            fetchUserBookings(userId.toInt())
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            filterBookingsByDate(selectedDate)
        }

        return view
    }


    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(
            bookings = emptyList(),
            onDeleteClick = { bookingItem -> deleteBooking(bookingItem) },
            onCommentClick = { bookingItem -> postReview(bookingItem) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = bookingAdapter
    }


    private fun fetchUserBookings(userId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val attractionBookings = fetchAttractionBookings(userId).filter { it.attractionBooking.status != "Canceled" }
                val hotelBookings = fetchHotelBookings(userId).filter { it.hotelBooking.status != "Canceled" }
                Log.d("ScheduleFragment", "Fetched Hotel Bookings: $hotelBookings") // ✅ 这里检查 HotelBooking 是否有数据


                withContext(Dispatchers.Main) {
                    // ✅  合并所有的 bookings
                    allBookings = attractionBookings + hotelBookings

                    Log.d("ScheduleFragment", "All Bookings: $allBookings")
                    // ✅  获取当前日历选中的日期，并筛选
                    filterBookingsByDate(getSelectedCalendarDate())
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 通过userId从后端获取attractionBooking的信息
     * 包含：attractionName 、 location 、 attractionImageUuid
     */
    private suspend fun fetchAttractionBookings(userId: Int): List<BookingItem.AttractionBookingItem> {
        return try {
            val response = apiService.getUserAttractionBookings(userId)
            if (response.isSuccessful) {
                val bookings = response.body() ?: emptyList()
                bookings.map { booking ->
                    val attraction = fetchAttraction(booking.attractionUuid)
                    BookingItem.AttractionBookingItem(
                        booking.copy(
                            attractionName = attraction?.name ?: "Unknown",
                            location = attraction?.address ?: "Unknown Location",
                            attractionImageUuid = attraction?.imageUuid ?: ""
                        )
                    )
                }

            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchHotelBookings(userId: Int): List<BookingItem.HotelBookingItem> {
        return try {
            val response = apiService.getUserHotelBookings(userId)
            Log.d("ScheduleFragment", "Hotel Booking API Response: ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                // 先直接打印 response.body()
                Log.d("ScheduleFragment", "Raw Response Body: ${response.body()}")

                val bookings = response.body() ?: emptyList()

                val processedBookings = bookings.map { booking ->
                    val hotel = fetchHotel(booking.hotelUuid)
                    BookingItem.HotelBookingItem(
                        HotelBooking(
                            hotelBookingId = booking.bookingId,
                            bookingId = booking.bookingId,
                            hotelUuid = booking.hotelUuid,
                            hotelName = hotel?.name ?: "Unknown",
                            location = hotel?.address ?: "Unknown Location",
                            checkInDate = booking.checkInDate,
                            checkOutDate = booking.checkOutDate,
                            roomType = booking.roomType ?: "未知房型",
                            guests = booking.guests,
                            totalAmount = booking.totalAmount,
                            hotelImageUuid = hotel?.imageUrl ?: "",
                            hotelId = booking.hotelId,
                            status = booking.status
                        )
                    )
                }
                Log.d("ScheduleFragment", "Processed Hotel Bookings: $processedBookings")
                processedBookings
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ScheduleFragment", "Error fetching hotel bookings: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun fetchAttraction(uuid: String?): Attraction? {
        if (uuid.isNullOrEmpty()) return null

        // 检查缓存，避免重复请求
        if (attractionCache.containsKey(uuid)) {
            return attractionCache[uuid]
        }

        return try {
            val response = attractionApiService.fetchAttractionByUUID(
                uuid = uuid,
                apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC",
                contentLanguage = "en"
            )

            if (response.isSuccessful) {
                val attractionDataList = response.body()?.data ?: emptyList()
                val attraction = attractionDataList.firstOrNull()?.let { attractionData ->
                    Attraction(
                        uuid = attractionData.uuid,
                        name = attractionData.name ?: "Unknown Attraction",
                        address = attractionData.address?.formattedAddress() ?: "Unknown Location",
                        description = "",
                        rate = 0.0,
                        price = "",
                        imageUuid = attractionData.thumbnails.firstOrNull()?.uuid ?: "" // ✅ 确保有图片
                    )
                }

                // 存入缓存，避免重复请求
                if (attraction != null) {
                    attractionCache[uuid] = attraction
                }
                attraction
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchHotel(uuid: String?): Hotel? {
        if (uuid.isNullOrEmpty()) return null
        if (hotelCache.containsKey(uuid)) return hotelCache[uuid]

        return try {
            val response = hotelApiService.fetchHotelByUUID(
                uuid = uuid,
                apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC",
                contentLanguage = "en"
            )

            if (response.isSuccessful) {
                val hotelDataList = response.body()?.data ?: emptyList()
                val hotel = hotelDataList.firstOrNull()?.let { hotelData ->
                    Hotel(
                        uuid = hotelData.uuid,
                        name = hotelData.name ?: "Unknown Attraction",
                        address = hotelData.address?.formattedAddress() ?: "Unknown Location",
                        description = "",
                        rating = 0.0.toString(),
                        price = "",
                        imageUrl = hotelData.thumbnails?.firstOrNull()?.uuid ?: "" // ✅ 确保有图片
                    )
                }
                hotel
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 💬 处理提交评论 (Post Review)
     */
    private fun postReview(bookingItem: BookingItem) {
        showReviewDialog(bookingItem)
    }


    /**
     * 🗑 处理删除预约 (Delete Booking)
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteBooking(bookingItem: BookingItem) {
        val bookingId = when (bookingItem) {
            is BookingItem.AttractionBookingItem -> bookingItem.attractionBooking.bookingId
            is BookingItem.HotelBookingItem -> bookingItem.hotelBooking.bookingId
            else -> return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = when (bookingItem) {
                    is BookingItem.AttractionBookingItem -> apiService.deleteAttractionBooking(bookingId)
                    is BookingItem.HotelBookingItem -> apiService.deleteHotelBooking(bookingId)
                    else -> return@launch
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("Booking deleted successfully! Refreshing bookings...")

                        // 重新获取用户预订数据
                        val userId = sessionManager.getUserIdFromPrefs()?.toIntOrNull()
                        if (userId != null) {
                            fetchUserBookings(userId)
                        }
                    } else {
                        showToast("Failed to delete booking. Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error deleting booking: ${e.message}")
                }
            }
        }
    }




    /**
     * 💬 处理提交评论
     */
    private fun showReviewDialog(bookingItem: BookingItem) {
        val userId = sessionManager.getUserIdFromPrefs()?.toInt() ?: return

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_review, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.review_rating_bar)
        val editTextReview = dialogView.findViewById<EditText>(R.id.review_comment)

        // **根据 BookingItem 类型，确定 itemId 和 itemType**
        val (itemId, bookingId, itemType) = when (bookingItem) {
            is BookingItem.AttractionBookingItem -> Triple(
                bookingItem.attractionBooking.attractionId,
                bookingItem.attractionBooking.bookingId,
                "Attraction"
            )
            is BookingItem.HotelBookingItem -> Triple(
                bookingItem.hotelBooking.hotelBookingId,
                bookingItem.hotelBooking.bookingId,
                "Hotel"
            )
            else -> {
                showToast("Invalid booking type for review!")
                return
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Leave a Review")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating
                val reviewText = editTextReview.text.toString()

                if (reviewText.isNotEmpty() && rating > 0) {
                    submitReview(itemId, bookingId, itemType, reviewText, rating)
                } else {
                    showToast("Review cannot be empty and rating must be greater than 0!")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * ✅ 提交评论到后端
     */
    private fun submitReview(itemId: Int, bookingId: Int, itemType: String, commentText: String, rating: Float) {
        val userId = sessionManager.getUserIdFromPrefs()?.toIntOrNull()
        if (userId == null) {
            showToast("User not logged in!")
            return
        }

        val reviewRequest = ReviewRequest(
            userId = userId,
            itemId = itemId, // ✅ 关联的 Attraction ID
            itemType = itemType,
            bookingId = bookingId, // ✅ 关联的 Booking ID
            rating = BigDecimal.valueOf(rating.toDouble()),
            comment = commentText
        )

        Log.d("SubmitReview", "Sending ReviewRequest: $reviewRequest")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.postReview(reviewRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("Review submitted successfully!")
                        Log.d("SubmitReview", "Review submitted successfully!")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        showToast("Failed to post review: $errorBody")
                        Log.e("SubmitReview", "Failed to post review: HTTP ${response.code()}, Error: $errorBody")
                    }
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                Log.e("SubmitReview", "Error submitting review: ${e.message}", e)
            }
        }
    }

    /**
     * 📆 过滤日期
     */
    private fun filterBookingsByDate(selectedDate: String) {
        filteredBookings = allBookings.filter {
            when (it) {
                is BookingItem.AttractionBookingItem -> it.attractionBooking.visitDate == selectedDate
//                is BookingItem.FlightBookingItem ->
                is BookingItem.HotelBookingItem -> it.hotelBooking.checkInDate == selectedDate
                else -> false
            }
        }
        bookingAdapter.updateBookings(filteredBookings)
    }

    /**
     * 🔔 显示 Toast
     */
    private fun showToast(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedCalendarDate(): String {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1 // `Calendar.MONTH` 从 0 开始
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        return String.format("%04d-%02d-%02d", year, month, day)
    }


}
