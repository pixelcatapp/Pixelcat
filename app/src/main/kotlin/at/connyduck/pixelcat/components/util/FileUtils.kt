package at.connyduck.pixelcat.components.util

import android.webkit.MimeTypeMap
import java.util.Locale

/**
 * tries to guess the mime type of a file path from the file extension.
 * @return the mime type, or null if it couldn't be determined
 */
fun getMimeType(filePath: String): String? {
    val extension = filePath.split('.').lastOrNull()?.toLowerCase(Locale.ROOT) ?: return null
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}
