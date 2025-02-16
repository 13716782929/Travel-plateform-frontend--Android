package iss.nus.edu.sg.mygo.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.NotificationAdapter
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.models.AttractionBooking
import iss.nus.edu.sg.mygo.models.HotelBooking
import iss.nus.edu.sg.mygo.api.service.AccommodationApiService
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import iss.nus.edu.sg.mygo.home.LoginActivity
import iss.nus.edu.sg.mygo.models.Notification
import kotlinx.coroutines.launch
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @ClassName NotificationFragment
 * @Description
 * @Author Wang Chang and Yao Yiyang
 * @StudentID A0310544R & A0294873L
 * @Date 2025/2/14
 * @Version 1.5
 */

class NotificationFragment : Fragment() {

    private lateinit var urgentNotificationLayout: View
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationList: MutableList<Notification>

    private lateinit var apiService: AttractionApiService

    private val sessionManager by lazy { SessionManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val isLoggedIn = sessionManager.isLoggedIn()
        val rootView = inflater.inflate(R.layout.notification_fragment, container, false)

        if (!isLoggedIn) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return null
        }

        // 初始化 UI 组件
        urgentNotificationLayout = rootView.findViewById(R.id.urgentNotificationLayout)
        notificationRecyclerView = rootView.findViewById(R.id.notification_cards)

        // 初始化 RecyclerView
        notificationList = mutableListOf()
        notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = NotificationAdapter(notificationList) { position -> }

        notificationRecyclerView.adapter = adapter

        apiService = AttractionApiService.create()


        // 异步加载通知数据
        lifecycleScope.launch {
            val userId = sessionManager.getUserIdFromPrefs()?.toInt() // 获取当前用户 ID
            if (userId==null) {
                //Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            } else {
                val notifications = generateNotifications(userId)
                notificationList.clear()
                notificationList.addAll(notifications)
                adapter.notifyDataSetChanged()
                if (notifications.any { it.title.contains("Canceled") }) {
                    showUrgentNotification()
                } else {
                    hideUrgentNotification()
                }
            }

        }

        return rootView
    }

    // **获取用户所有 AttractionBooking 和 HotelBooking，并生成通知**
    private suspend fun generateNotifications(userId: Int): List<Notification> {
        val notifications = mutableListOf<Notification>()

        val attractionBookings = getUserAttractionBookings(userId)
        val hotelBookings = getUserHotelBookings(userId)

        for (booking in attractionBookings) {
            val attractionTime = booking.visitTime
            val attractionName = booking.attractionUuid?.let { fetchAttractionNameByUuid(it) } ?: booking.attractionName
            notifications.add(createAttractionNotification(booking, attractionName, attractionTime))
        }

        for (booking in hotelBookings) {
            val checkinDate = booking.checkInDate
            val hotelName = booking.hotelUuid?.let { fetchHotelNameByUuid(it) } ?: booking.hotelName
            notifications.add(createHotelNotification(booking, hotelName, checkinDate))
        }

        return notifications
    }

    // **从 API 获取用户的 AttractionBooking**
    private suspend fun getUserAttractionBookings(userId: Int): List<AttractionBooking> {
        return try {
            val response = UserApiService.create().getUserAttractionBookings(userId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // **从 API 获取用户的 HotelBooking**
    private suspend fun getUserHotelBookings(userId: Int): List<HotelBooking> {
        return try {
            val response = UserApiService.create().getUserHotelBookings(userId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // **生成 Attraction 预订通知**
    private fun createAttractionNotification(booking: AttractionBooking, attractionName: String, attractionTime: String): Notification {
        val title = when (booking.status) {
            "Confirmed" -> "Upcoming Attraction Visit"
            "Canceled" -> "Canceled Attraction Booking"
            "Pending" -> "Pending Attraction Approval"
            else -> "Attraction Notification"
        }

        val message = when (booking.status) {
            "Confirmed" -> "Your visit to $attractionName is confirmed!\nDate: ${booking.visitDate}\nTime: ${booking.visitTime}\nTickets: ${booking.numberOfTickets}"
            "Canceled" -> "Your attraction visit to $attractionName has been canceled."
            "Pending" -> "Your booking for $attractionName is pending approval."
            else -> ""
        }

        val color = when(booking.status) {
            "Confirmed" -> "green_notification"
            "Canceled" -> "red_notification"
            "Pending" -> "orange_notification"
            else -> "orange_notification"
        }

        return Notification(title, getColorByName(color), message, attractionTime)
    }

    private suspend fun fetchHotelNameByUuid(uuid: String): String {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.stb.gov.sg/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(AccommodationApiService::class.java)
            val apiKey = "pRUJyzyyzpRd557ynCs7JRZtoKYr6PPC"

            val response = apiService.searchAccommodationByUUID(apiKey, "en", "uuids", uuid).execute()
            if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
                response.body()?.data?.firstOrNull()?.name ?: "Unknown Hotel"
            } else {
                "Unknown Hotel"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown Hotel"
        }
    }

    private suspend fun fetchAttractionNameByUuid(uuid: String): String {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.stb.gov.sg/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(AttractionApiService::class.java)
            val apiKey = "pRUJyzyyzpRd557ynCs7JRZtoKYr6PPC"

            val response = apiService.fetchAttractionByUUID("uuids", uuid, apiKey, "en", )
            if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
                response.body()?.data?.firstOrNull()?.name ?: "Unknown Attraction"
            } else {
                "Unknown Attraction"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown Attraction"
        }
    }

    // **显示紧急通知**
    // **生成 Hotel 预订通知**
    private fun createHotelNotification(booking: HotelBooking, hotelName: String, checkinDate: String): Notification {
        val title = when (booking.status) {
            "Confirmed" -> "Upcoming Hotel Stay"
            "Canceled" -> "Canceled Hotel Booking"
            "Pending" -> "Pending Hotel Approval"
            else -> "Hotel Notification"
        }

        val message = when (booking.status) {
            "Confirmed" -> "Your hotel booking at $hotelName (Room: ${booking.roomType}) is confirmed!\nCheck-in: ${booking.checkInDate}\nCheck-out: ${booking.checkOutDate}\nTotal: $${booking.totalAmount}"
            "Canceled" -> "Your hotel booking at $hotelName has been canceled."
            "Pending" -> "Your hotel booking at $hotelName is pending approval."
            else -> ""
        }


        return Notification(title, getColorByName("green_notification"), message, checkinDate)
    }

    // **显示紧急通知**
    private fun showUrgentNotification() {
        urgentNotificationLayout.visibility = View.VISIBLE
    }

    // **隐藏紧急通知**
    private fun hideUrgentNotification() {
        urgentNotificationLayout.visibility = View.GONE
    }

    // **获取颜色**
    private fun getColorByName(colorName: String): Int {
        val context = requireContext()
        val colorResId = context.resources.getIdentifier(colorName, "color", context.packageName)
        return if (colorResId != 0) {
            ContextCompat.getColor(context, colorResId)
        } else {
            ContextCompat.getColor(context, R.color.orange_notification)
        }
    }
}
