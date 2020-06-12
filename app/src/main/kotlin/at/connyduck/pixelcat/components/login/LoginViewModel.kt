package at.connyduck.pixelcat.components.login

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.connyduck.pixelcat.config.Config
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.db.entitity.AccountAuthData
import at.connyduck.pixelcat.network.FediverseApi
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import java.util.Locale
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val fediverseApi: FediverseApi,
    private val accountManager: AccountManager
) : ViewModel() {

    val loginState = MutableLiveData<LoginModel>().apply {
        value = LoginModel(state = LoginState.NO_ERROR)
    }

    @MainThread
    fun startLogin(input: String) {

        val domainInput = canonicalizeDomain(input)

        try {
            HttpUrl.Builder().host(domainInput).scheme("https").build()
        } catch (e: IllegalArgumentException) {
            loginState.value = LoginModel(input, LoginState.INVALID_DOMAIN)
            return
        }

        val exceptionMatch = Config.domainExceptions.any { exception ->
            domainInput.equals(exception, true) || domainInput.endsWith(".$exception", true)
        }

        if (exceptionMatch) {
            loginState.value = LoginModel(input, LoginState.AUTH_ERROR)
            return
        }

        loginState.value = LoginModel(input, LoginState.LOADING)

        viewModelScope.launch {
            fediverseApi.authenticateAppAsync(
                domain = domainInput,
                clientName = "Pixelcat",
                clientWebsite = Config.website,
                redirectUris = Config.oAuthRedirect,
                scopes = Config.oAuthScopes
            ).fold(
                { appData ->
                    loginState.postValue(LoginModel(input, LoginState.SUCCESS, domainInput, appData.clientId, appData.clientSecret))
                },
                {
                    loginState.postValue(LoginModel(input, LoginState.AUTH_ERROR))
                }
            )
        }
    }

    @MainThread
    fun authCode(authCode: String) {
        viewModelScope.launch {
            val loginModel = loginState.value!!

            fediverseApi.fetchOAuthToken(
                domain = loginModel.domain!!,
                clientId = loginModel.clientId!!,
                clientSecret = loginModel.clientSecret!!,
                redirectUri = Config.oAuthRedirect,
                code = authCode
            ).fold(
                { tokenResponse ->
                    val authData = AccountAuthData(
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken,
                        tokenExpiresAt = tokenResponse.createdAt ?: 0 + (
                            tokenResponse.expiresIn
                                ?: 0
                            ),
                        clientId = loginModel.clientId,
                        clientSecret = loginModel.clientSecret
                    )
                    accountManager.addAccount(loginModel.domain, authData)
                    loginState.postValue(loginState.value?.copy(state = LoginState.SUCCESS_FINAL))
                },
                {
                }
            )
        }
    }

    private fun canonicalizeDomain(domain: String): String {
        // Strip any schemes out.
        var s = domain.replaceFirst("http://", "")
            .replaceFirst("https://", "")
        // If a username was included (e.g. username@example.com), just take what's after the '@'.
        val at = s.lastIndexOf('@')
        if (at != -1) {
            s = s.substring(at + 1)
        }
        return s.trim().toLowerCase(Locale.ROOT)
    }
}
