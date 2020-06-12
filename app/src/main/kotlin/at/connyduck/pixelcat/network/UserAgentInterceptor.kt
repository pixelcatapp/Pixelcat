package at.connyduck.pixelcat.network

import android.os.Build
import at.connyduck.pixelcat.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithUserAgent = chain.request()
            .newBuilder()
            .header("User-Agent", "PixelCat/${BuildConfig.VERSION_NAME} Android/${Build.VERSION.RELEASE}")
            .build()
        return chain.proceed(requestWithUserAgent)
    }

}