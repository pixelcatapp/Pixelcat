package at.connyduck.pixelcat.components.util

sealed class UiState<T>(open val data: T?)

class Loading<T> (override val data: T? = null) : UiState<T>(data)

class Success<T> (override val data: T? = null) : UiState<T>(data)

class Error<T> (
    override val data: T? = null,
    val errorMessage: String? = null,
    var consumed: Boolean = false,
    val cause: Throwable? = null
) : UiState<T>(data)
