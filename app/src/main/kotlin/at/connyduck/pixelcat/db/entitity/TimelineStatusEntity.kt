

package at.connyduck.pixelcat.db.entitity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverters
import at.connyduck.pixelcat.db.Converters
import at.connyduck.pixelcat.model.Attachment
import at.connyduck.pixelcat.model.Status
import java.util.Date

@Entity(primaryKeys = ["accountId", "id"])
@TypeConverters(Converters::class)
data class StatusEntity(
    val accountId: Long,
    val id: String,
    val url: String?, // not present if it's reblog
    @Embedded(prefix = "a_") val account: TimelineAccountEntity,
    val content: String,
    val createdAt: Date,
    val reblogsCount: Int,
    val favouritesCount: Int,
    val reblogged: Boolean,
    val favourited: Boolean,
    val sensitive: Boolean,
    val spoilerText: String,
    val visibility: Status.Visibility,
    val attachments: List<Attachment>,

    val mediaPosition: Int,
    val mediaVisible: Boolean
)

fun Status.toEntity(accountId: Long, mediaPosition: Int = 0, mediaVisible: Boolean = this.sensitive) = StatusEntity(
    accountId = accountId,
    id = id,
    url = actionableStatus.url,
    account = actionableStatus.account.toEntity(accountId),
    content = actionableStatus.content,
    createdAt = actionableStatus.createdAt,
    reblogsCount = actionableStatus.reblogsCount,
    favouritesCount = actionableStatus.favouritesCount,
    reblogged = actionableStatus.reblogged,
    favourited = actionableStatus.favourited,
    sensitive = actionableStatus.sensitive,
    spoilerText = actionableStatus.spoilerText,
    visibility = actionableStatus.visibility,
    attachments = actionableStatus.attachments,
    mediaPosition = mediaPosition,
    mediaVisible = mediaVisible
)
