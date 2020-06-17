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


package at.connyduck.pixelcat.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Attachment(
    val id: String,
    val url: String,
    @Json(name = "preview_url") val previewUrl: String,
    val meta: MetaData?,
    val type: Type,
    val description: String?
) : Parcelable {

    enum class Type {
        @Json(name = "image")
        IMAGE,
        @Json(name = "gifv")
        GIFV,
        @Json(name = "video")
        VIDEO,
        @Json(name = "audio")
        AUDIO,
        @Json(name = "unknown")
        UNKNOWN
    }

    /*class MediaTypeDeserializer : JsonDeserializer<Type> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, classOfT: java.lang.reflect.Type, context: JsonDeserializationContext): Type {
            return when (json.toString()) {
                "\"image\"" -> Type.IMAGE
                "\"gifv\"" -> Type.GIFV
                "\"video\"" -> Type.VIDEO
                "\"audio\"" -> Type.AUDIO
                else -> Type.UNKNOWN
            }
        }
    }*/

    /**
     * The meta data of an [Attachment].
     */
    @JsonClass(generateAdapter = true)
    @Parcelize
    data class MetaData(
        val focus: Focus?,
        val duration: Float?
    ) : Parcelable

    /**
     * The Focus entity, used to specify the focal point of an image.
     *
     * See here for more details what the x and y mean:
     *   https://github.com/jonom/jquery-focuspoint#1-calculate-your-images-focus-point
     */
    @JsonClass(generateAdapter = true)
    @Parcelize
    data class Focus(
        val x: Float,
        val y: Float
    ) : Parcelable
}
