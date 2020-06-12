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

            val currentAccount = runBlocking { accountManager.activeAccount() }
            val builder = originalRequest.newBuilder()

            val instanceHeader = originalRequest.header(FediverseApi.DOMAIN_HEADER)
            if (instanceHeader != null) {
                // use domain explicitly specified in custom header
                builder.url(swapHost(originalRequest.url, instanceHeader))
                builder.removeHeader(FediverseApi.DOMAIN_HEADER)
            } else if (currentAccount != null) {
                //use domain of current account
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
