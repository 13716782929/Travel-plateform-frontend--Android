package iss.nus.edu.sg.mygo.models

/*
Class name: User Model
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date:
Version
*/


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