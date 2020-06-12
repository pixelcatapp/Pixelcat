package at.connyduck.pixelcat.network.calladapter

import android.util.Log
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

internal class NetworkResponseCall<S : Any>(
    private val delegate: Call<S>
) : Call<NetworkResponse<S>> {

    override fun enqueue(callback: Callback<NetworkResponse<S>>) {
        return delegate.enqueue(object : Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                val body = response.body()

                val errorbody = response.errorBody()?.string()
                if (response.isSuccessful) {
                    if (body != null) {
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(NetworkResponse.Success(body))
                        )
                    } else {
                        // Response is successful but the body is null
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(
                                NetworkResponse.Failure(
                                    NetworkResponseError.ApiError(
                                        response.code()
                                    )
                                )
                            )
                        )
                    }
                } else {
                    callback.onResponse(
                        this@NetworkResponseCall,
                        Response.success(
                            NetworkResponse.Failure(
                                NetworkResponseError.ApiError(
                                    response.code()
                                )
                            )
                        )
                    )
                }
            }

            override fun onFailure(call: Call<S>, throwable: Throwable) {
                Log.d("NetworkResponseCall", "Network response failed", throwable)
                val networkResponse = when (throwable) {
                    is IOException -> NetworkResponse.Failure(
                        NetworkResponseError.NetworkError(
                            throwable
                        )
                    )
                    else -> NetworkResponse.Failure(NetworkResponseError.UnknownError(throwable))
                }
                callback.onResponse(this@NetworkResponseCall, Response.success(networkResponse))
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun clone() = NetworkResponseCall(delegate.clone())

    override fun isCanceled() = delegate.isCanceled

    override fun cancel() = delegate.cancel()

    override fun execute(): Response<NetworkResponse<S>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support synchronized execution")
    }

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}
