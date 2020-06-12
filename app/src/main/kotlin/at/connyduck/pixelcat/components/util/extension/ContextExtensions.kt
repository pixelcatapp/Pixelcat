package at.connyduck.pixelcat.components.util.extension

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

fun Context.getDisplayWidthInPx(): Int {
    val metrics = DisplayMetrics()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

@ColorInt
fun Context.getColorForAttr(@AttrRes attr: Int): Int {
    val value = TypedValue()
    return if (this.theme.resolveAttribute(attr, value, true)) {
        value.data
    } else {
        throw IllegalArgumentException()
    }
}
