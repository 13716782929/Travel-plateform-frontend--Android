package iss.nus.edu.sg.mygo.api.models

import java.math.BigDecimal

data class Preference(
    val travelType: String,
    val budgetRange: BigDecimal,
    val language: String
)
