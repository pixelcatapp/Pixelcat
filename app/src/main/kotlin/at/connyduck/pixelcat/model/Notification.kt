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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Notification(
    val type: Type,
    val id: String,
    val account: Account,
    val status: Status?
) {

    @JsonClass(generateAdapter = false)
    enum class Type {
        UNKNOWN,
        @Json(name = "mention")
        MENTION,
        @Json(name = "reblog")
        REBLOG,
        @Json(name = "favourite")
        FAVOURITE,
        @Json(name = "follow")
        FOLLOW,
        @Json(name = "follow_request")
        FOLLOW_REQUEST,
        @Json(name = "poll")
        POLL
    }
}
