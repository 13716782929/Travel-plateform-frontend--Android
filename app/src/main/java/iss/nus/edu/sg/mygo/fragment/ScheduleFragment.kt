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
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.AttractionBooking
import iss.nus.edu.sg.mygo.models.Booking
import iss.nus.edu.sg.mygo.models.BookingItem
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal


class ScheduleFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private val bookings = mutableListOf<Booking>()
    private val attractionBookings = mutableListOf<AttractionBooking>()
    private val attractionDetails = mutableMapOf<Int, Attraction>()

    private val apiService = UserApiService.create()
    private val sessionManager by lazy { SessionManager(requireContext()) }
    private val attractionApiService = AttractionApiService.create()

    private var selectedDate: String = ""
    private var allBookings: List<BookingItem> = emptyList() // 用户所有booking的信息
    private var filteredBookings: List<BookingItem> = emptyList() // 日期筛选后的bookings
    // Attraction 缓存，避免重复请求
    private val attractionCache = mutableMapOf<String, Attraction>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val isLoggedIn = sessionManager.isLoggedIn()
        showToast("🔍 DEBUG: isLoggedIn = $isLoggedIn") // ✅ 打印登录状态

        if (!isLoggedIn) {
            println("🔍 DEBUG: User not logged in, redirecting to LoginActivity")
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
                val response = apiService.getUserAttractionBookings(userId)
                if (response.isSuccessful) {
                    val bookings = response.body() ?: emptyList()
                    val bookingItems = mutableListOf<BookingItem>()

                    for (booking in bookings) {
                        val attraction = fetchAttraction(booking.attractionUuid)

                        // 生成 BookingItem，并修正 `visitTime` 格式
                        bookingItems.add(
                            BookingItem.AttractionBookingItem(
                                booking.copy(
                                    attractionName = attraction?.name ?: "Unknown",
                                    location = attraction?.address ?: "Unknown Location",
                                    attractionImageUuid = attraction?.imageUuid ?: "" // ✅ 赋值景点图片 UUID
                                )
                            )
                        )
                    }

                    withContext(Dispatchers.Main) {
                        allBookings = bookingItems

                        // ✅ 获取当前日历选中的日期，并筛选
                        val calendarDate = getSelectedCalendarDate()
                        filterBookingsByDate(calendarDate)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to fetch bookings!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
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

    /**
     * 💬 处理提交评论 (Post Review)
     */
    private fun postReview(bookingItem: BookingItem.AttractionBookingItem) {
        showReviewDialog(bookingItem)
    }


    /**
     * 🗑 处理删除预约 (Delete Booking)
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteBooking(bookingItem: BookingItem.AttractionBookingItem) {
        val bookingId = bookingItem.attractionBooking.bookingId

        lifecycleScope.launch(Dispatchers.Main) {
            // 先从 `allBookings` 里删除该 `Booking`
            allBookings = allBookings.filterNot {
                it is BookingItem.AttractionBookingItem && it.attractionBooking.bookingId == bookingId
            }


            filterBookingsByDate(selectedDate)

            bookingAdapter.updateBookings(filteredBookings)

            recyclerView.post {
                bookingAdapter.notifyDataSetChanged()
            }
            showToast("Booking removed!")
        }
    }


    /**
     * 💬 处理提交评论
     */
    private fun showReviewDialog(bookingItem: BookingItem.AttractionBookingItem) {
        val userId = sessionManager.getUserIdFromPrefs()?.toInt() ?: return
        val bookingId = bookingItem.attractionBooking.bookingId
        val attractionId = bookingItem.attractionBooking.attractionId // ✅ 修复: 确保获取 attractionId

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_review, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.review_rating_bar)
        val editTextReview = dialogView.findViewById<EditText>(R.id.review_comment)

        AlertDialog.Builder(requireContext())
            .setTitle("Leave a Review")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating
                val reviewText = editTextReview.text.toString()

                if (reviewText.isNotEmpty() && rating > 0) {
                    submitReview(attractionId, bookingId, reviewText, rating)
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
    private fun submitReview(itemId: Int, bookingId: Int, commentText: String, rating: Float) {
        val userId = sessionManager.getUserIdFromPrefs()?.toIntOrNull()
        if (userId == null) {
            showToast("User not logged in!")
            return
        }

        val reviewRequest = ReviewRequest(
            userId = userId,
            itemId = itemId, // ✅ 关联的 Attraction ID
            itemType = "Attraction",
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
//                is BookingItem.HotelBookingItem ->
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
