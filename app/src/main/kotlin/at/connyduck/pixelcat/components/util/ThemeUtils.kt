package at.connyduck.pixelcat.components.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
fun Context.getColorForAttr(@AttrRes attr: Int): Int {
    val value = TypedValue()
    if(theme.resolveAttribute(attr, value, true)) {
        return value.data
    }
    throw IllegalStateException("Attribute not found")
}