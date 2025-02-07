package iss.nus.edu.sg.mygo.models

data class AttractionResponse(
    val data: List<AttractionItem>
)

data class AttractionItem(
    val name: String,
    val description: String?,
    val address: String?,
    val location: Location?,
    val images: List<ImageItem>?
)

data class Location(
    val latitude: Double?,
    val longitude: Double?
)

data class ImageItem(
    val url: String
)
