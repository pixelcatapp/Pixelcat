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
import at.connyduck.pixelcat.db.Converters
import at.connyduck.pixelcat.model.Notification

@Entity(primaryKeys = ["accountId", "id"])
@TypeConverters(Converters::class)
data class NotificationEntity(
    val accountId: Long,
    val type: Notification.Type,
    val id: String,
    @Embedded(prefix = "a_") val account: TimelineAccountEntity,
    @Embedded(prefix = "s_") val status: StatusEntity?
)

fun Notification.toEntity(accountId: Long) = NotificationEntity(
    accountId = accountId,
    type = type,
    id = id,
    account = account.toEntity(accountId),
    status = status?.toEntity(accountId, 0, false)
)
