package iss.nus.edu.sg.mygo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.NotificationAdapter
import iss.nus.edu.sg.mygo.models.Notification

/***
 * author: Wang Chang
 * date: 5/2/25
 * */
class NotificationAdapter(
    private val notifications: List<Notification>,
    private val onItemClick: (Int) -> Unit // 点击事件
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_card, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.titleTextView.text = notification.title
        holder.titleTextView.setTextColor(notification.color)
        holder.contentTextView.text = notification.content
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    // ViewHolder用于绑定数据
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.notificationTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.notificationContent)
    }
}
