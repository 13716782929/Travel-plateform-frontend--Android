package iss.nus.edu.sg.mygo.models

import com.google.gson.annotations.SerializedName

// 顶级 API 响应对象
data class AccommodationResponse(
    @SerializedName("status") val status: Status,
    @SerializedName("data") val data: List<Accommodation>,
    @SerializedName("paginationLinks") val paginationLinks: PaginationLinks?,
    @SerializedName("totalRecords") val totalRecords: Int,
    @SerializedName("retrievedRecords") val retrievedRecords: Int
)

// 状态信息
data class Status(
    @SerializedName("code") val code: Int,
    @SerializedName("name") val name: String,
    @SerializedName("message") val message: String
)

// 住宿信息
data class Accommodation(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("location") val location: Location,
    @SerializedName("address") val address: Address,
    @SerializedName("thumbnails") val thumbnails: List<Image>?,
    @SerializedName("images") val images: List<Image>?,
    @SerializedName("videos") val videos: List<Video>?,
    @SerializedName("documents") val documents: List<Document>?,
    @SerializedName("source") val source: String?,
    @SerializedName("metadata") val metadata: Metadata,
    @SerializedName("categoryDescription") val categoryDescription: String?,
    @SerializedName("dataset") val dataset: String?,
    @SerializedName("leadInRoomRates") val leadInRoomRates: String?,
    @SerializedName("reviews") val reviews: List<Review>?,
    @SerializedName("companyDisplayName") val companyDisplayName: String?,
    @SerializedName("supportedLanguage") val supportedLanguage: List<String>?,
    @SerializedName("leadInRoomSize") val leadInRoomSize: String?,
    @SerializedName("noOfRooms") val noOfRooms: Int?,
    @SerializedName("amenities") val amenities: String?,
    @SerializedName("contact") val contact: Contact?,
    @SerializedName("nearestMrtStation") val nearestMrtStation: String?,
    @SerializedName("officialWebsite") val officialWebsite: String?,
    @SerializedName("officialEmail") val officialEmail: String?,
    @SerializedName("temporarilyClosed") val temporarilyClosed: String?,
    @SerializedName("links") val links: List<ApiLink>?
)

// 地址
data class Address(
    @SerializedName("block") val block: String?,
    @SerializedName("streetName") val streetName: String?,
    @SerializedName("floorNumber") val floorNumber: String?,
    @SerializedName("unitNumber") val unitNumber: String?,
    @SerializedName("buildingName") val buildingName: String?,
    @SerializedName("postalCode") val postalCode: String?
)

// 位置
data class Location(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

// 图片
data class Image(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("url") val url: String?,
    @SerializedName("libraryUuid") val libraryUuid: String?,
    @SerializedName("primaryFileMediumUuid") val primaryFileMediumUuid: String?
)

// 视频
data class Video(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("url") val url: String?
)

// 文档
data class Document(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("url") val url: String?
)

// 联系信息
data class Contact(
    @SerializedName("primaryContactNo") val primaryContactNo: String?,
    @SerializedName("secondaryContactNo") val secondaryContactNo: String?
)

// API 相关链接
data class ApiLink(
    @SerializedName("href") val href: String,
    @SerializedName("rel") val rel: String,
    @SerializedName("method") val method: String
)

// 元数据
data class Metadata(
    @SerializedName("updatedDate") val updatedDate: String,
    @SerializedName("createdDate") val createdDate: String
)

// 分页链接
data class PaginationLinks(
    @SerializedName("self") val self: String?,
    @SerializedName("first") val first: String?,
    @SerializedName("next") val next: String?
)

// 评价（如果 API 未来有评价数据）
data class Review(
    @SerializedName("author") val author: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("rating") val rating: Double?
)
