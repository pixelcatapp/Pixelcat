package at.connyduck.pixelcat.db.entitity

import androidx.room.Entity
import at.connyduck.pixelcat.model.Account

@Entity(
        primaryKeys = ["serverId", "timelineUserId"]
)
data class TimelineAccountEntity(
        val serverId: Long,
        val id: String,
        val localUsername: String,
        val username: String,
        val displayName: String,
        val url: String,
        val avatar: String
)

fun Account.toEntity(serverId: Long) = TimelineAccountEntity(
        serverId = serverId,
        id = id,
        localUsername = localUsername,
        username = username,
        displayName =  displayName,
        url = url,
        avatar = avatar
)