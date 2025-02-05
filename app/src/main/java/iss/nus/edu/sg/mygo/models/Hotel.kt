package iss.nus.edu.sg.mygo.models

data class Hotel(
    val name: String,
    val address: String,
    val rating: String,
    val price: String,
    val imageUrl: String? = null, // 新增字段：图片 URL
    val imageResId: Int? = null  // 允许本地资源 ID 作为备用
)
