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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import at.connyduck.pixelcat.config.Config
import android.webkit.WebViewClient
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivityLoginWebViewBinding

class LoginWebViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            val toolbarParams = binding.loginToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top
            WindowInsetsCompat.CONSUMED
        }

        binding.loginToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val domain = intent.getStringExtra(EXTRA_DOMAIN)!!
        val clientId = intent.getStringExtra(EXTRA_CLIENT_ID)!!
        val clientSecret = intent.getStringExtra(EXTRA_CLIENT_SECRET)!!

        val endpoint = "/oauth/authorize"
        val parameters = mapOf(
            "client_id" to clientId,
            "redirect_uri" to Config.oAuthRedirect,
            "response_type" to "code",
            "scope" to Config.oAuthScopes
        )

        val url = "https://" + domain + endpoint + "?" + toQueryString(parameters)

        binding.loginWebView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url.scheme == Config.oAuthScheme && request.url.host == Config.oAuthHost) {
                    loginSuccess(request.url.getQueryParameter("code").orEmpty())
                    return true
                }

                return false
            }
        }
        binding.loginWebView.loadUrl(url)
    }

    private fun loginSuccess(authCode: String) {
        val successIntent = Intent().apply {
            putExtra(RESULT_AUTHORIZATION_CODE, authCode)
        }
        setResult(Activity.RESULT_OK, successIntent)
        finish()
    }

    private fun toQueryString(parameters: Map<String, String>): String {
        return parameters.map { "${it.key}=${Uri.encode(it.value)}" }
            .joinToString("&")
    }

    companion object {
        const val RESULT_AUTHORIZATION_CODE = "authCode"

        private const val EXTRA_DOMAIN = "domain"
        private const val EXTRA_CLIENT_ID = "clientId"
        private const val EXTRA_CLIENT_SECRET = "clientSecret"

        fun newIntent(domain: String, clientId: String, clientSecret: String, context: Context): Intent {
            return Intent(context, LoginWebViewActivity::class.java).apply {
                putExtra(EXTRA_DOMAIN, domain)
                putExtra(EXTRA_CLIENT_ID, clientId)
                putExtra(EXTRA_CLIENT_SECRET, clientSecret)
            }
        }
    }
}
