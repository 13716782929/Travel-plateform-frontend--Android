package com.example.registernewuser.models

class User (
    val userId: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val status: String? = null,
    val gender: String? = null,
    val countryOfOrigin: String? = null,
    val dateOfBirth: String? = null,
    val createTime: String? = null
)