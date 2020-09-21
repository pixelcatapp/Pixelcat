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

package at.connyduck.pixelcat.components.login

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginModel(
    val input: CharSequence = "",
    val state: LoginState = LoginState.NO_ERROR,
    val domain: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null
) : Parcelable

enum class LoginState { // TODO rename this stuff so it makes sense
    LOADING,
    NO_ERROR,
    NETWORK_ERROR,
    INVALID_DOMAIN,
    AUTH_ERROR,
    SUCCESS,
    SUCCESS_FINAL
}
