/*
 * Copyright (C) 2020 Conny Duck
 *
 * This file is part of Pixelcat.
 *
 * Pixelcat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pixelcat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.connyduck.pixelcat.db.entitity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverters
import at.connyduck.pixelcat.db.RoomConverter
import at.connyduck.pixelcat.model.Attachment
import at.connyduck.pixelcat.model.Status
import java.util.Date

@Entity(primaryKeys = ["accountId", "id"])
@TypeConverters(RoomConverter::class)
data class StatusEntity(
    val accountId: Long,
    val id: String,
    val actionableId: String,
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

fun Status.toEntity(accountId: Long, mediaPosition: Int = 0, mediaVisible: Boolean = !this.sensitive) = StatusEntity(
    accountId = accountId,
    id = id,
    actionableId = actionableStatus.id,
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
