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

package at.connyduck.pixelcat.dagger

import at.connyduck.pixelcat.BuildConfig
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.model.Notification
import at.connyduck.pixelcat.network.FediverseApi
import at.connyduck.pixelcat.network.InstanceSwitchAuthInterceptor
import at.connyduck.pixelcat.network.RefreshTokenAuthenticator
import at.connyduck.pixelcat.network.UserAgentInterceptor
import at.connyduck.pixelcat.network.calladapter.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(accountManager: AccountManager): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor())
            .addInterceptor(InstanceSwitchAuthInterceptor(accountManager))
            .authenticator(RefreshTokenAuthenticator(accountManager))
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(Notification.Type::class.java, EnumJsonAdapter.create(Notification.Type::class.java).withUnknownFallback(Notification.Type.UNKNOWN))
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(httpClient: OkHttpClient, moshi: Moshi): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://" + FediverseApi.PLACEHOLDER_DOMAIN)
            .client(httpClient)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit): FediverseApi = retrofit.create()
}
