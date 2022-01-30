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

import android.webkit.MimeTypeMap
import java.util.Locale

/**
 * tries to guess the mime type of a file path from the file extension.
 * @return the mime type, or null if it couldn't be determined
 */
fun getMimeType(filePath: String): String? {
    val extension = filePath.split('.').lastOrNull()?.lowercase(Locale.ROOT) ?: return null
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}
