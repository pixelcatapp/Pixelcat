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

package at.connyduck.pixelcat

import androidx.appcompat.app.AppCompatDelegate
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import at.connyduck.pixelcat.components.settings.AppSettings
import at.connyduck.pixelcat.dagger.DaggerAppComponent
import coil.Coil
import coil.ImageLoader
import coil.util.CoilUtils
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import okhttp3.OkHttpClient
import javax.inject.Inject

class PixelcatApplication : DaggerApplication() {

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var okhttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(appSettings.getNightMode())

        EmojiCompat.init(BundledEmojiCompatConfig(this))

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .okHttpClient(
                    okhttpClient.newBuilder()
                        .cache(CoilUtils.createDefaultCache(this))
                        .build()
                )
                .build()
        )
    }

    override fun applicationInjector(): AndroidInjector<PixelcatApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}
