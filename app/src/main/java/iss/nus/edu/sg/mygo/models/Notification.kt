package iss.nus.edu.sg.mygo.models

data class Notification(
    val title: String,     // 通知标题
    val color: Int,        // 通知颜色
    val message: String    // 具体消息内容
)
