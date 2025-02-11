package iss.nus.edu.sg.mygo.api.models

import com.google.gson.annotations.SerializedName

    data class AttractionRecommendation(
        @SerializedName("attractionId")
        val attractionId: Int,  // 如果你的前端字段名和后端不匹配，确保后端 `attractionId` 可选

        @SerializedName("uuid")
        val uuid: String?,  // 对应后端的 `uuid`

        @SerializedName("attractionName")
        val name: String?,  // 允许为空

        @SerializedName("location")
        val address: String?,  // 允许为空

        @SerializedName("description")
        val description: String?,  // 允许为空

        @SerializedName("ticketType")
        val ticketType: Map<String, String>?,  // 允许为空

        @SerializedName("openingHours")
        val openingHours: Map<String, String>?,  // 允许为空

        @SerializedName("ticketAvailability")
        val ticketAvailability: Map<String, Int>?,  // 允许为空

    )


