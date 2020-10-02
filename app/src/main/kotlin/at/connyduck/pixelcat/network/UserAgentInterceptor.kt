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

package at.connyduck.pixelcat.network

import android.os.Build
import at.connyduck.pixelcat.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttp
import okhttp3.Response

class UserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithUserAgent = chain.request()
            .newBuilder()
            .header("User-Agent", "Pixelcat/${BuildConfig.VERSION_NAME} Android/${Build.VERSION.RELEASE} okhttp/${OkHttp.VERSION}")
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}
