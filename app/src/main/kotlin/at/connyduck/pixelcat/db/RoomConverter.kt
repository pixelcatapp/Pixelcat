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

package at.connyduck.pixelcat.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import at.connyduck.pixelcat.model.Attachment
import at.connyduck.pixelcat.model.Notification
import at.connyduck.pixelcat.model.Status
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@ProvidedTypeConverter
@Singleton
class RoomConverter @Inject constructor (
    private val moshi: Moshi
) {

    @TypeConverter
    fun visibilityToString(visibility: Status.Visibility) = visibility.name

    @TypeConverter
    fun stringToVisibility(visibility: String) = Status.Visibility.valueOf(visibility)

    @TypeConverter
    fun attachmentListToJson(attachmentList: List<Attachment>?): String {
        val type = Types.newParameterizedType(
            List::class.java,
            Attachment::class.java
        )
        return moshi.adapter<List<Attachment>>(type).toJson(attachmentList)
    }

    @TypeConverter
    fun jsonToAttachmentList(attachmentListJson: String?): List<Attachment>? {
        if (attachmentListJson == null) {
            return null
        }
        val type = Types.newParameterizedType(
            List::class.java,
            Attachment::class.java
        )
        return moshi.adapter<List<Attachment>>(type).fromJson(attachmentListJson)
    }

    @TypeConverter
    fun dateToLong(date: Date) = date.time

    @TypeConverter
    fun longToDate(date: Long) = Date(date)

    @TypeConverter
    fun notificationTypeToString(type: Notification.Type) = type.name

    @TypeConverter
    fun stringToNotificationType(type: String) = Notification.Type.valueOf(type)
}
