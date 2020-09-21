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

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NetworkResponseCallTest {

    private val backingCall = TestCall<String>()
    private val networkCall = NetworkResponseCall(backingCall)

    @Test
    fun `should throw an error when invoking 'execute'`() {
        assertThrows<UnsupportedOperationException> {
            networkCall.execute()
        }
    }

    @Test
    fun `should delegate properties to backing call`() {
        with(networkCall) {
            assertEquals(isExecuted, backingCall.isExecuted)
            assertEquals(isCanceled, backingCall.isCanceled)
            assertEquals(request(), backingCall.request())
        }
    }

    @Test
    fun `should return new instance when cloned`() {
        val clonedCall = networkCall.clone()
        assert(clonedCall !== networkCall)
    }

    @Test
    fun `should cancel backing call as well when cancelled`() {
        networkCall.cancel()
        assert(backingCall.isCanceled)
    }

    @Test
    fun `should parse successful call as NetworkResponse Success`() {
        val body = "Test body"
        networkCall.enqueue(
            object : Callback<NetworkResponse<String>> {
                override fun onResponse(
                    call: Call<NetworkResponse<String>>,
                    response: Response<NetworkResponse<String>>
                ) {
                    assertTrue(response.isSuccessful)
                    assertEquals(
                        response.body(),
                        NetworkResponse.Success(body)
                    )
                }

                override fun onFailure(call: Call<NetworkResponse<String>>, t: Throwable) {
                    throw IllegalStateException()
                }
            }
        )
        backingCall.complete(body)
    }

    @Test
    fun `should parse call with 404 error code as ApiError`() {
        val errorCode = 404
        val errorBody = "not found"
        networkCall.enqueue(
            object : Callback<NetworkResponse<String>> {
                override fun onResponse(
                    call: Call<NetworkResponse<String>>,
                    response: Response<NetworkResponse<String>>
                ) {
                    assertEquals(
                        response.body(),
                        NetworkResponse.Failure(NetworkResponseError.ApiError(errorCode))
                    )
                }

                override fun onFailure(call: Call<NetworkResponse<String>>, t: Throwable) {
                    throw IllegalStateException()
                }
            }
        )

        backingCall.complete(Response.error(errorCode, errorBody.toResponseBody()))
    }

    @Test
    fun `should parse call with IOException as NetworkError`() {
        val exception = IOException()
        networkCall.enqueue(
            object : Callback<NetworkResponse<String>> {
                override fun onResponse(
                    call: Call<NetworkResponse<String>>,
                    response: Response<NetworkResponse<String>>
                ) {
                    assertEquals(
                        response.body(),
                        NetworkResponse.Failure(NetworkResponseError.NetworkError(exception))
                    )
                }

                override fun onFailure(call: Call<NetworkResponse<String>>, t: Throwable) {
                    throw IllegalStateException()
                }
            }
        )

        backingCall.completeWithException(exception)
    }
}
