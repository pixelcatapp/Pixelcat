package at.connyduck.pixelcat.network.calladapter

import java.io.IOException

sealed class NetworkResponse<out A : Any> {

    data class Success<T : Any>(val body: T) : NetworkResponse<T>()

    data class Failure(val reason: NetworkResponseError) : NetworkResponse<Nothing>()

    inline fun <B> fold(onSuccess: (A) -> B, onFailure: (NetworkResponseError) -> B): B = when (this) {
        is Success -> onSuccess(body)
        is Failure -> onFailure(reason)
    }
}

sealed class NetworkResponseError : Throwable() {

    data class ApiError(val code: Int) : NetworkResponseError()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : NetworkResponseError()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable?) : NetworkResponseError()
}
