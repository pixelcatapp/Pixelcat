package at.connyduck.pixelcat.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusContext (
    val ancestors: List<Status>,
    val descendants: List<Status>
)
