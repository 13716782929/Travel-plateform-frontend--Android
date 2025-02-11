package iss.nus.edu.sg.mygo.models

data class Attraction(
    val uuid: String,
    val name: String,
    val address: String,
    val description: String,
    val rate: Double,
    val price: String,
//    val openTime: String,
    var imageUuid: String // ✅ 只存 UUID，不存 URL
)

