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

package at.connyduck.pixelcat.components.util.extension

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

fun Context.getDisplayWidthInPx(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowManager.currentWindowMetrics.bounds.width()
    } else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(metrics)
        metrics.widthPixels
    }
}

@ColorInt
fun Context.getColorForAttr(@AttrRes attr: Int): Int {
    val value = TypedValue()
    if (this.theme.resolveAttribute(attr, value, true)) {
        return value.data
    }
    throw IllegalArgumentException("Attribute not found")
}
