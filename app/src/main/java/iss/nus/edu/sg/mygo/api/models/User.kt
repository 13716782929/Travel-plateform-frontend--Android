package iss.nus.edu.sg.mygo.api.models

import com.google.gson.annotations.SerializedName

/*
Class name:  data class User
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 11 Feb 2025
Version: 1.0
*/

// class represents the data your backend will return.
data class User(
    @SerializedName("user_name") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_no") val phoneNumber: String,
    @SerializedName("full_name") val realName: String
)