package at.connyduck.pixelcat.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Relationship (
    val id: String,
    val following: Boolean,
    @Json(name = "followed_by") val followedBy: Boolean,
    val blocking: Boolean,
    val muting: Boolean,
    val requested: Boolean,
    @Json(name = "showing_reblogs") val showingReblogs: Boolean
)
