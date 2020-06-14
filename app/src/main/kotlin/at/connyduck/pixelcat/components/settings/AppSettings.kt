package at.connyduck.pixelcat.components.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import at.connyduck.pixelcat.R
import javax.inject.Inject

class AppSettings @Inject constructor (
    private val sharedPrefs: SharedPreferences,
    private val context: Context
) {

    @StyleRes
    fun getAppColorStyle(): Int {
        val appColorPref = sharedPrefs.getString(
            context.getString(R.string.key_pref_app_color),
            context.getString(R.string.key_pref_app_color_default)
        )

        return when (appColorPref) {
            context.getString(R.string.key_pref_app_color_warm) -> R.style.Warm
            context.getString(R.string.key_pref_app_color_cold) -> R.style.Cold
            else -> throw IllegalStateException()
        }
    }

    @AppCompatDelegate.NightMode
    fun getNightMode(): Int {
        val nightModePref = sharedPrefs.getString(
            context.getString(R.string.key_pref_night_mode),
            context.getString(R.string.key_pref_night_mode_default)
        )

        return when (nightModePref) {
            context.getString(R.string.key_pref_night_mode_off) -> MODE_NIGHT_NO
            context.getString(R.string.key_pref_night_mode_on) -> MODE_NIGHT_YES
            context.getString(R.string.key_pref_night_mode_follow_system) -> MODE_NIGHT_FOLLOW_SYSTEM
            else -> throw IllegalStateException()
        }
    }

    fun isBlackNightMode(): Boolean {
        return sharedPrefs.getBoolean(
            context.getString(R.string.key_pref_black_night_mode),
            context.resources.getBoolean(R.bool.pref_title_black_night_mode_default)
        )
    }

    fun useSystemFont(): Boolean {
        return sharedPrefs.getBoolean(
            context.getString(R.string.key_pref_system_font),
            context.resources.getBoolean(R.bool.pref_title_system_font_default)
        )
    }
}

private fun SharedPreferences.getNonNullString(key: String, default: String): String {
    return getString(key, default) ?: default
}
