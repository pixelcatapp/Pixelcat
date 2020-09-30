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

package at.connyduck.pixelcat.network.calladapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

class ApiTest {

    private var mockWebServer = MockWebServer()

    private lateinit var api: TestApi

    @BeforeEach
    fun setup() {
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(OkHttpClient())
            .build()
            .create(TestApi::class.java)
    }

    @AfterEach
    fun shutdown() {
        mockWebServer.shutdown()
    }

    private fun mockResponse(responseCode: Int, body: String = "") = MockResponse()
        .setResponseCode(responseCode)
        .setBody(body)

    @Test
    fun `should return the correct test object`() {
        val response = mockResponse(
            200,
            """
                {
                    "lets": "test",
                    "test": 1
                }
            """
        )

        mockWebServer.enqueue(response)

        val responseObject = runBlocking {
            api.testEndpoint()
        }

        assertEquals(
            NetworkResponse.Success(TestResponseClass("not", 1)),
            responseObject
        )
    }

    @Test
    fun `should return a ApiError failure when the server returns error 500`() {
        val errorCode = 500
        val response = mockResponse(errorCode)

        mockWebServer.enqueue(response)

        val responseObject = runBlocking {
            api.testEndpoint()
        }

        assertEquals(
            NetworkResponse.Failure(NetworkResponseError.ApiError(errorCode)),
            responseObject
        )
    }

    @Test
    fun `should return a NetworkError failure when the network fails`() {
        mockWebServer.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST })
        val responseObject = runBlocking {
            api.testEndpoint()
        }

        assertEquals(
            NetworkResponse.Failure(
                NetworkResponseError.NetworkError(
                    object : IOException() {
                        override fun equals(other: Any?): Boolean {
                            return (other is IOException)
                        }
                    }
                )
            ),
            responseObject
        )
    }
}
