package iss.nus.edu.sg.mygo.models

data class Review(
    val reviewId: Int,         // 评论ID
    val userId: Int,     // 用户ID
    val itemId: Int,     // 关联的景点/酒店/航班ID
    val itemType: String, // 评论类型，例如 "ATTRACTION"
    val rating: Float,   // 评分 (1.0 - 5.0)
    val comment: String, // 评论内容
    var status: ReviewStatus = ReviewStatus.SHOW,
    val createdAt: String // 创建时间
)

enum class ReviewStatus(val displayName: String){
    HIDE("hide"),
    SHOW("show"),
}