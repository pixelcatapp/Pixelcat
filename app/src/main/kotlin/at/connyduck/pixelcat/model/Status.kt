
package at.connyduck.pixelcat.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Status(
    val id: String,
    val url: String?, // not present if it's reblog
    val account: Account,
    @Json(name = "in_reply_to_id") val inReplyToId: String?,
    @Json(name = "in_reply_to_account_id") val inReplyToAccountId: String?,
    val reblog: Status?,
    val content: String,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "reblogs_count") val reblogsCount: Int,
    @Json(name = "favourites_count") val favouritesCount: Int,
    val reblogged: Boolean,
    val favourited: Boolean,
    val sensitive: Boolean,
    @Json(name = "spoiler_text") val spoilerText: String,
    val visibility: Visibility,
    @Json(name = "media_attachments") val attachments: List<Attachment>,
    val mentions: List<Mention>,
    val application: Application?
) {

    val actionableId: String
        get() = reblog?.id ?: id

    val actionableStatus: Status
        get() = reblog ?: this

    enum class Visibility {
        UNKNOWN,
        @Json(name = "public")
        PUBLIC,
        @Json(name = "unlisted")
        UNLISTED,
        @Json(name = "private")
        PRIVATE,
        @Json(name = "direct")
        DIRECT
    }

    fun rebloggingAllowed(): Boolean {
        return (visibility != Visibility.DIRECT && visibility != Visibility.UNKNOWN)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val status = other as Status?
        return id == status?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    @JsonClass(generateAdapter = true)
    data class Mention (
        val id: String,
        val url: String,
        val acct: String,
        val username: String
    )

    @JsonClass(generateAdapter = true)
    data class Application (
        val name: String,
        val website: String?
    )

}
