package at.connyduck.pixelcat.components.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import at.connyduck.pixelcat.config.Config
import android.webkit.WebViewClient
import at.connyduck.pixelcat.databinding.ActivityLoginWebViewBinding

class LoginWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val domain = intent.getStringExtra(EXTRA_DOMAIN)
        val clientId = intent.getStringExtra(EXTRA_CLIENT_ID)
        val clientSecret = intent.getStringExtra(EXTRA_CLIENT_SECRET)

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
