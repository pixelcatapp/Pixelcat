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

package at.connyduck.pixelcat.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import java.lang.IllegalStateException

inline fun <reified T> Fragment.arg(key: String): T {
    val value = arguments?.get(key)

    if (value !is T) {
        throw IllegalStateException("Argument $key is of wrong type")
    }
    return value
}

inline fun Fragment.withArgs(argsBuilder: Bundle.() -> Unit): Fragment {
    this.arguments = Bundle().apply(argsBuilder)
    return this
}
