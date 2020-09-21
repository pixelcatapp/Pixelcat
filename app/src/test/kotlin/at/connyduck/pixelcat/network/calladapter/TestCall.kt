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

import java.io.InterruptedIOException
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestCall<T> : Call<T> {
    private var executed = false
    private var canceled = false
    private var callback: Callback<T>? = null
    private var request = Request.Builder().url("http://example.com").build()

    fun completeWithException(t: Throwable) {
        synchronized(this) {
            callback?.onFailure(this, t)
        }
    }

    fun complete(body: T) = complete(Response.success(body))

    fun complete(response: Response<T>) {
        synchronized(this) {
            callback?.onResponse(this, response)
        }
    }

    override fun enqueue(callback: Callback<T>) {
        synchronized(this) {
            this.callback = callback
        }
    }

    override fun isExecuted() = synchronized(this) { executed }
    override fun isCanceled() = synchronized(this) { canceled }
    override fun clone() = TestCall<T>()

    override fun cancel() {
        synchronized(this) {
            if (canceled) return
            canceled = true

            val exception = InterruptedIOException("canceled")
            callback?.onFailure(this, exception)
        }
    }

    override fun execute(): Response<T> {
        throw UnsupportedOperationException("Network call does not support synchronous execution")
    }

    override fun request() = request
    override fun timeout() = Timeout()
}
