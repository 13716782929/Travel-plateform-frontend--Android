package iss.nus.edu.sg.mygo.models

import java.math.BigDecimal

data class Preference(
    val preferenceId: Int,
    val userId: Int,  // 绑定的用户ID
    val travelType: TravelType = TravelType.Single, // 旅行类型
    val budgetRange: BigDecimal?, // 预算范围
    val language: String = "en" // 语言 (默认 "en")
)

enum class TravelType {
    Single, Couple, Family
}
