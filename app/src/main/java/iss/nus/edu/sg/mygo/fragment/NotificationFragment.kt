package iss.nus.edu.sg.mygo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.NotificationAdapter
import iss.nus.edu.sg.mygo.models.Notification

/**
 * @ClassName NotificationFragment
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/28
 * @Version 1.3
 */

class NotificationFragment : Fragment() {

    private lateinit var urgentNotificationLayout: View
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationList: List<Notification>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.notification_fragment, container, false)

        // 获取紧急通知布局和RecyclerView
        urgentNotificationLayout = rootView.findViewById(R.id.urgentNotificationLayout)
        notificationRecyclerView = rootView.findViewById(R.id.notification_cards)

        // 检查是否有紧急事件并显示相应的通知
        if (hasEmergencyEvent()) {
            showUrgentNotification()
        } else {
            hideUrgentNotification()
        }

        // 模拟通知数据
        notificationList = listOf(
            Notification("Out of Date", getColorByName("red_notification"), "Your flight SQ12138 is out of data! \n" +
                    "Time of taking off at :  8:30 , 8th April"),
            Notification("Upcoming Flight", getColorByName("orange_notification"),"Your travel plan at Sentosa may rain at your booking time !"),
            Notification("Completed Hotel" ,getColorByName("green_notification"), "Your Booking Hotel Room502 has done ! \n" +
                    "Time of taking off at :  8:30 , 8th April"),
            Notification("Out of Date", getColorByName("red_notification"), "Your flight SQ12138 is out of data! \n" +
                    "Time of taking off at :  8:30 , 8th April"),
            Notification("Upcoming Flight", getColorByName("orange_notification"),"Your travel plan at Sentosa may rain at your booking time !"),
            Notification("Completed Hotel" ,getColorByName("green_notification"), "Your Booking Hotel Room502 has done ! \n" +
                    "Time of taking off at :  8:30 , 8th April")
        )
        // 设置RecyclerView
        notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        notificationRecyclerView.adapter = NotificationAdapter(notificationList) { position ->
            // 点击事件处理
            val clickedNotification = notificationList[position]
            // 可以在这里进行处理，例如跳转到通知详情页
        }

        return rootView
    }

    // 判断是否有紧急事件
    private fun hasEmergencyEvent(): Boolean {
        // 这里你可以根据实际情况判断是否有紧急事件
        return true // 假设有紧急事件
    }

    // 显示紧急通知
    private fun showUrgentNotification() {
        urgentNotificationLayout.visibility = View.VISIBLE
    }

    // 隐藏紧急通知
    private fun hideUrgentNotification() {
        urgentNotificationLayout.visibility = View.GONE
    }

    private fun getColorByName(colorName: String): Int {
        val context = requireContext()
        val colorResId = context.resources.getIdentifier(colorName, "color", context.packageName)
        return if (colorResId != 0) {
            ContextCompat.getColor(context, colorResId)
        } else {
            // 如果找不到颜色，返回默认颜色
            ContextCompat.getColor(context, R.color.orange_notification)
        }
    }
}
