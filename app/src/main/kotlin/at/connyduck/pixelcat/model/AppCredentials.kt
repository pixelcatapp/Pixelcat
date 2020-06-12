package at.connyduck.pixelcat.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppCredentials(
    @Json(name = "client_id") val clientId: String,
    @Json(name = "client_secret") val clientSecret: String
)
