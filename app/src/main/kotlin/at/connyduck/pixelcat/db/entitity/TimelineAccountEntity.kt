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
    displayName = displayName,
    url = url,
    avatar = avatar
)
