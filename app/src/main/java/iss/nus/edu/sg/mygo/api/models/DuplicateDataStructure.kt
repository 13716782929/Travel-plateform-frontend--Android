package iss.nus.edu.sg.mygo.api.models

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("code") val code: Int,
    @SerializedName("name") val name: String,
    @SerializedName("message") val message: String
)

data class Location(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

// 地址 address
data class Address(
    @SerializedName("block")
    val block: String,

    @SerializedName("streetName")
    val streetName: String,

    @SerializedName("floorNumber")
    val floorNumber: String,

    @SerializedName("unitNumber")
    val unitNumber: String,

    @SerializedName("buildingName")
    val buildingName: String,

    @SerializedName("postalCode")
    val postalCode: String
){
    fun formattedAddress(): String {
        // 用 listOf() 过滤空值，并用 ", " 连接
        return listOfNotNull(
            block?.takeIf { it.isNotBlank() },
            streetName?.takeIf { it.isNotBlank() },
            floorNumber?.takeIf { it.isNotBlank() },
            unitNumber?.takeIf { it.isNotBlank() },
            buildingName?.takeIf { it.isNotBlank() },
            postalCode?.takeIf { it.isNotBlank() }
        ).joinToString(", ") // 用 ", " 作为分隔符
    }
}


// 图片 image
data class Image(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("url") val url: String?,
    @SerializedName("libraryUuid") val libraryUuid: String?,
    @SerializedName("primaryFileMediumUuid") val primaryFileMediumUuid: String?
)

// 元数据 Metadata
// in duplicate class : DuplicateDataStructure.kt
data class Metadata(
    @SerializedName("updatedDate")
    val updatedDate: String,

    @SerializedName("createdDate")
    val createdDate: String
)

// 分页链接 PaginationLinks
// in duplicate class : DuplicateDataStructure.kt
data class PaginationLinks(
    @SerializedName("self") val self: String?,
    @SerializedName("first") val first: String?,
    @SerializedName("next") val next: String?
)

// 联系信息 Contact
// in duplicate class : DuplicateDataStructure.kt
data class Contact(
    @SerializedName("primaryContactNo") val primaryContactNo: String?,
    @SerializedName("secondaryContactNo") val secondaryContactNo: String?
)
