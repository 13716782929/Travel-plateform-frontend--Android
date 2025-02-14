package iss.nus.edu.sg.mygo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import iss.nus.edu.sg.mygo.models.Notification
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private lateinit var urgentNotificationLayout: View
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationList: MutableList<Notification>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.notification_fragment, container, false)

        // 初始化 UI 组件
        urgentNotificationLayout = rootView.findViewById(R.id.urgentNotificationLayout)
        notificationRecyclerView = rootView.findViewById(R.id.notification_cards)

        // 初始化 RecyclerView
        notificationList = mutableListOf()
        notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = NotificationAdapter(notificationList) { position -> }

        notificationRecyclerView.adapter = adapter

        // 异步加载通知数据
        lifecycleScope.launch {
            val userId = getUserIdFromSession() // 获取当前用户 ID
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

        return rootView
    }

    // **获取用户 ID**
    private fun getUserIdFromSession(): Int {
        // 这里应该从 SharedPreferences 或者 SessionManager 获取当前用户 ID
        return 1 // 假设返回用户 ID 1，实际应用需改成真实的逻辑
    }

    // **获取用户所有 AttractionBooking 和 HotelBooking，并生成通知**
    private suspend fun generateNotifications(userId: Int): List<Notification> {
        val notifications = mutableListOf<Notification>()

        val attractionBookings = getUserAttractionBookings(userId)
        val hotelBookings = getUserHotelBookings(userId)

        for (booking in attractionBookings) {
            val attractionName = booking.attractionUuid ?: booking.attractionName
            notifications.add(createAttractionNotification(booking, attractionName))
        }

        for (booking in hotelBookings) {
            val hotelName = booking.hotelUuid ?: booking.hotelName
            notifications.add(createHotelNotification(booking, hotelName))
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
    private fun createAttractionNotification(booking: AttractionBooking, attractionName: String): Notification {
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

        return Notification(title, getColorByName("orange_notification"), message)
    }

    // **生成 Hotel 预订通知**
    private fun createHotelNotification(booking: HotelBooking, hotelName: String): Notification {
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

        return Notification(title, getColorByName("green_notification"), message)
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
