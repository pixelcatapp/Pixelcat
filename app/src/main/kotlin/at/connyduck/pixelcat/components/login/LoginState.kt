package at.connyduck.pixelcat.components.login

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginModel(
    val input: CharSequence = "",
    val state: LoginState = LoginState.NO_ERROR,
    val domain: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null
) : Parcelable

enum class LoginState { // TODO rename this stuff so it makes sense
    LOADING, NO_ERROR, NETWORK_ERROR, INVALID_DOMAIN, AUTH_ERROR, SUCCESS, SUCCESS_FINAL
}
