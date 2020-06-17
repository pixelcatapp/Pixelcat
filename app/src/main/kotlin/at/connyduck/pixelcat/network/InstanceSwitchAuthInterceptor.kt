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

import at.connyduck.pixelcat.db.AccountManager
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class InstanceSwitchAuthInterceptor(private val accountManager: AccountManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        // only switch domains if the request comes from retrofit
        if (originalRequest.url.host == FediverseApi.PLACEHOLDER_DOMAIN) {

            val builder = originalRequest.newBuilder()

            // when using retrofit we want json responses
            builder.addHeader("Accept", "application/json")

            val currentAccount = runBlocking { accountManager.activeAccount() }

            val instanceHeader = originalRequest.header(FediverseApi.DOMAIN_HEADER)
            if (instanceHeader != null) {
                // use domain explicitly specified in custom header
                builder.url(swapHost(originalRequest.url, instanceHeader))
                builder.removeHeader(FediverseApi.DOMAIN_HEADER)
            } else if (currentAccount != null) {
                // use domain of current account
                builder.url(swapHost(originalRequest.url, currentAccount.domain))
                    .header(
                        "Authorization",
                        "Bearer ${currentAccount.auth.accessToken}"
                    )
            }
            val newRequest = builder.build()

            return chain.proceed(newRequest)
        } else {
            return chain.proceed(originalRequest)
        }
    }

    private fun swapHost(url: HttpUrl, host: String): HttpUrl {
        return url.newBuilder().host(host).build()
    }
}
