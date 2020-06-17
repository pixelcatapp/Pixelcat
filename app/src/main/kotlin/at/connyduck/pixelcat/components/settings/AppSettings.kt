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
