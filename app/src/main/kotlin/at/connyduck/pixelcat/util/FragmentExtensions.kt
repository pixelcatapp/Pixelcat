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
