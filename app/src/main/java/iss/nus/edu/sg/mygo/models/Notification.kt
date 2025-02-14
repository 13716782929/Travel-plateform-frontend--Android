package iss.nus.edu.sg.mygo.models

import java.time.LocalDateTime

/**
 * @ClassName Notification
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/6
 * @Version 1.3
 */
// 用于存储通知的数据模型类
data class Notification(
    val title: String,
    val color: Int,
    val content: String,
    val time: String
)