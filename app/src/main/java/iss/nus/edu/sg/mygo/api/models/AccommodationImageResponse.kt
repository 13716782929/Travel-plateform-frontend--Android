package iss.nus.edu.sg.mygo.api.models

data class AccommodationImageResponse(
    val status: Status1,
    val data: List<MediaItem>

)

data class Status1(
    val code: Int,
    val name: String,
    val message: String
)

data class MediaItem(
    val uuid: String,
    val parentMediaUuid: String?,
    val name: String,
    val description: String?,
    val tags: String?,
    val category: String?,
    val subCategory: List<String>?,
    val fileSize: Long,
    val duration: Int,
    val url: String,
    val libraryUuid: String,
    val libraryName: String,
    val fileType: String,
    val mimeType: String,
    val mediaType: String,
    val source: String?,
    val orientation: String,
    val metadata: Metadata1,
    val fileTypeDescription: String,
    val imageResolution: String,
    val companyDisplayName: String,
    val links: List<Link>
)

data class Metadata1(
    val createdDate: String,
    val updatedDate: String
)

data class Link(
    val href: String,
    val rel: String,
    val method: String
)


