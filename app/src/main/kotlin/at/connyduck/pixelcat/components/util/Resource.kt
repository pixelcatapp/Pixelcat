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

package at.connyduck.pixelcat.components.util

sealed class UiState<T>(open val data: T?)

class Loading<T> (override val data: T? = null) : UiState<T>(data)

class Success<T> (override val data: T? = null) : UiState<T>(data)

class Error<T> (
    override val data: T? = null,
    val errorMessage: String? = null,
    var consumed: Boolean = false,
    val cause: Throwable? = null
) : UiState<T>(data)
