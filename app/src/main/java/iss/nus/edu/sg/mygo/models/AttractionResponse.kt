package iss.nus.edu.sg.mygo.models
/***
 * author: Siti Alifah Binte Yahya
 * StudentID:  &A0295324B
 * date: 9 Feb 25
 * */

import com.google.gson.annotations.SerializedName
import iss.nus.edu.sg.mygo.enum.TicketType

// Since API responses return JSON, we need data classes to store structured data.

// Top-level response from the API
data class AttractionResponse(
    @SerializedName("data") val attractions: List<AttractionItem>
)


// Each attraction item in the list
data class AttractionItem(
    @SerializedName("UUID") val uuid: String, //maps to UUID in our ERD
    @SerializedName("attractionName") val name: String, //attractionName in ERD
    @SerializedName("location") val location: AttractionLocation, //maps to location
    @SerializedName("description") val description: String?, //Description in ERD
    @SerializedName("openTime") val openTime: String?,
    @SerializedName("ticketAvailability") val ticketAvailability: Boolean?,
    @SerializedName("ticketType") val ticketType: String?, //TicketType in ERD
    @SerializedName("ticketPrice") val ticketPrice: String?, //TicketPrice in ERD
    @SerializedName("images") val imageResId: List<ImageItem>? //list of images
) {


}

//Location details of the attraction
data class AttractionLocation(
    @SerializedName("latitude") val laltitude: Double,
    @SerializedName("longtitude") val longtitude: Double,
    @SerializedName("address") val address: Address? // nested address object
)

data class AttractionAddress(
    @SerializedName("roadName") val roadName: String?,
    @SerializedName("postalCode") val postalCode: String?
)

data class ImageItem(
    @SerializedName("url") val url: String //image URL
)


