package iss.nus.edu.sg.mygo.models

import java.math.BigDecimal
import com.google.gson.annotations.SerializedName

data class Review(
    val reviewId: Int,
    val userId: Int,
    val itemType: ItemType,
    val itemId: Int,
    val bookingId: Int,
    val rating: BigDecimal,
    val comment: String,
    val status: ReviewStatus,
    val createdAt: String,  // ✅ 确保 Gson 正确解析时间
    val updatedAt: String   // ✅ 确保 Gson 正确解析时间
)

enum class ItemType {
    @SerializedName("Flight") Flight,
    @SerializedName("Hotel") Hotel,
    @SerializedName("Attraction") Attraction
}

enum class ReviewStatus {
    @SerializedName("hide") HIDE,
    @SerializedName("show") SHOW;

    companion object {
        fun fromString(value: String): ReviewStatus {
            return when (value.lowercase()) {
                "hide" -> HIDE
                "show" -> SHOW
                else -> throw IllegalArgumentException("Unknown ReviewStatus: $value")
            }
        }
    }
}
