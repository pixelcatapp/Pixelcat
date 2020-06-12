package at.connyduck.pixelcat.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessToken(
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "refresh_token") val refreshToken: String?,
        @Json(name = "expires_in") val expiresIn: Long?,
        @Json(name = "created_at") val createdAt: Long?
)
