package at.connyduck.pixelcat.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewStatus(
    val status: String,
    //@Json(name = "spoiler_text") val warningText: String,
    @Json(name = "in_reply_to_id") val inReplyToId: String?,
    val visibility: String,
    val sensitive: Boolean,
    @Json(name = "media_ids") val mediaIds: List<String>?
)

