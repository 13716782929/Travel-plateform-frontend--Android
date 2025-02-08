package iss.nus.edu.sg.mygo.models

data class Hotel(
    val uuid: String? = null,
    val name: String,
    val address: String,
    val rating: String,
    val price: String,
    val description: String? = null,
    val imageUrl: String? = null, // 新增字段：图片media uuid
    val imageResId: Int? = null  // 允许本地资源 ID 作为备用
)
