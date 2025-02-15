package iss.nus.edu.sg.mygo.api.models

data class UserProfile(
    val userId: Int,
    val name: String,
    val email: String,
    val preference: Preference?
)
