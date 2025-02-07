package iss.nus.edu.sg.mygo.api.models

import com.google.gson.annotations.SerializedName

data class AttractionResponse(
    @SerializedName("status") val status: Status,
    @SerializedName("data") val data: List<AttractionData>,
    @SerializedName("paginationLinks") val paginationLinks: PaginationLinks,
    @SerializedName("totalRecords") val totalRecords: Int,
    @SerializedName("retrievedRecords") val retrievedRecords: Int
)



// attraction detail data
data class AttractionData(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("description") val description: String,
    @SerializedName("body") val body: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("location") val location: Location,
    @SerializedName("address") val address: Address,
    @SerializedName("thumbnails") val thumbnails: List<Image>,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("videos") val videos: List<Any>,
    @SerializedName("documents") val documents: List<Any>,
    @SerializedName("source") val source: String,
    @SerializedName("metadata") val metadata: Metadata,
    @SerializedName("categoryDescription") val categoryDescription: String,
    @SerializedName("dataset") val dataset: String,
    @SerializedName("reviews") val reviews: List<Any>,
    @SerializedName("pricing") val pricing: Pricing,
    @SerializedName("companyDisplayName") val companyDisplayName: String,
    @SerializedName("supportedLanguage") val supportedLanguage: List<String>,
    @SerializedName("amenities") val amenities: String,
    @SerializedName("businessHour") val businessHour: List<BusinessHour>,
    @SerializedName("contact") val contact: Contact,
    @SerializedName("nearestMrtStation") val nearestMrtStation: String,
    @SerializedName("officialWebsite") val officialWebsite: String,
    @SerializedName("officialEmail") val officialEmail: String,
    @SerializedName("staYear") val staYear: String,
    @SerializedName("admissionInfo") val admissionInfo: String,
    @SerializedName("ticketed") val ticketed: String,
    @SerializedName("group") val group: String,
    @SerializedName("temporarilyClosed") val temporarilyClosed: String,
    @SerializedName("businessHourNotes") val businessHourNotes: BusinessHourNotes
)

// location
// in duplicate class : DuplicateDataStructure.kt

// address
// in duplicate class : DuplicateDataStructure.kt

// 图片 image
// in duplicate class : DuplicateDataStructure.kt

// 元数据 Metadata
// in duplicate class : DuplicateDataStructure.kt

// 分页链接 PaginationLinks
// in duplicate class : DuplicateDataStructure.kt

data class Pricing(
    @SerializedName("others")
    val others: String
){
    fun formattedPrice(): String {
        // 根据实际需要拼接地址信息
        return others
    }
}

data class BusinessHour(
    @SerializedName("sequenceNumber") val sequenceNumber: Int,
    @SerializedName("day") val day: String,
    @SerializedName("daily") val daily: Boolean,
    @SerializedName("openTime") val openTime: String,
    @SerializedName("closeTime") val closeTime: String,
    @SerializedName("description") val description: String
)

// 联系信息 Contact
// in duplicate class : DuplicateDataStructure.kt

data class BusinessHourNotes(
    @SerializedName("notes") val notes: String
)

