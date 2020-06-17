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

import android.os.Parcel
import android.os.Parcelable
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.util.Date

@JsonClass(generateAdapter = true)
data class Account(
    val id: String,
    @Json(name = "username") val localUsername: String,
    @Json(name = "acct") val username: String,
    @Json(name = "display_name") val displayName: String,
    val note: String,
    val url: String,
    val avatar: String,
    val header: String,
    val locked: Boolean = false,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "following_count") val followingCount: Int,
    @Json(name = "statuses_count") val statusesCount: Int,
    val source: AccountSource?,
    val bot: Boolean,
    //  val emojis: List<Emoji>,  // nullable for backward compatibility
    val fields: List<Field>?, // nullable for backward compatibility
    val moved: Account?

) {

    val name: String
        get() = if (displayName.isEmpty()) {
            localUsername
        } else displayName

    fun isRemote(): Boolean = this.username != this.localUsername
}

@JsonClass(generateAdapter = true)
@Parcelize
data class AccountSource(
    //  val privacy: Status.Visibility,
    val sensitive: Boolean,
    val note: String,
    val fields: List<StringField>?
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Field(
    val name: String,
    //  val value: @WriteWith<SpannedParceler>() Spanned,
    @Json(name = "verified_at") val verifiedAt: Date?
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class StringField(
    val name: String,
    val value: String
) : Parcelable

object SpannedParceler : Parceler<Spanned> {
    override fun create(parcel: Parcel): Spanned = HtmlCompat.fromHtml(parcel.readString() ?: "", HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)

    override fun Spanned.write(parcel: Parcel, flags: Int) {
        parcel.writeString(HtmlCompat.toHtml(this, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL))
    }
}
