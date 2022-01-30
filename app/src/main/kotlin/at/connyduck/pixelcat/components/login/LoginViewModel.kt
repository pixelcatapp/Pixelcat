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

package at.connyduck.pixelcat.components.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.connyduck.pixelcat.config.Config
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.db.entitity.AccountAuthData
import at.connyduck.pixelcat.network.FediverseApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import java.util.Locale
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val api: FediverseApi,
    private val accountManager: AccountManager
) : ViewModel() {

    private val loginState = MutableStateFlow(LoginModel(state = LoginState.NO_ERROR))

    fun observe(): Flow<LoginModel> = loginState

    fun startLogin(input: String) {
        viewModelScope.launch {
            val domainInput = canonicalizeDomain(input)

            try {
                HttpUrl.Builder().host(domainInput).scheme("https").build()
            } catch (e: IllegalArgumentException) {
                loginState.value = LoginModel(input, LoginState.INVALID_DOMAIN)
                return@launch
            }

            val exceptionMatch = Config.domainExceptions.any { exception ->
                domainInput.equals(exception, true) || domainInput.endsWith(".$exception", true)
            }

            if (exceptionMatch) {
                loginState.value = LoginModel(input, LoginState.AUTH_ERROR)
                return@launch
            }

            loginState.value = LoginModel(input, LoginState.LOADING)

            api.authenticateAppAsync(
                domain = domainInput,
                clientName = "Pixelcat",
                clientWebsite = Config.website,
                redirectUris = Config.oAuthRedirect,
                scopes = Config.oAuthScopes
            ).fold(
                { appData ->
                    loginState.value = LoginModel(input, LoginState.SUCCESS, domainInput, appData.clientId, appData.clientSecret)
                },
                {
                    loginState.value = LoginModel(input, LoginState.AUTH_ERROR)
                }
            )
        }
    }

    fun authCode(authCode: String) {
        viewModelScope.launch {
            val loginModel = loginState.value

            api.fetchOAuthToken(
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
                    loginState.value = loginState.value.copy(state = LoginState.SUCCESS_FINAL)
                },
                {
                    loginState.value = loginState.value.copy(state = LoginState.AUTH_ERROR)
                }
            )
        }
    }

    fun removeError() {
        loginState.value = loginState.value.copy(state = LoginState.NO_ERROR)
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
        return s.trim().lowercase(Locale.ROOT)
    }
}
