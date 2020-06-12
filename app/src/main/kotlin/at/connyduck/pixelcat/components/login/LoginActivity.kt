package at.connyduck.pixelcat.components.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import at.connyduck.pixelcat.components.main.MainActivity
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.about.AboutActivity
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.components.settings.SettingsActivity
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.ActivityLoginBinding
import at.connyduck.pixelcat.util.viewBinding
import javax.inject.Inject


class LoginActivity : BaseActivity(), Observer<LoginModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(ActivityLoginBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.loginContainer.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            val toolbarParams = binding.loginToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top

            insets.consumeSystemWindowInsets()
        }

        setSupportActionBar(binding.loginToolbar)

        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
        }

        loginViewModel.loginState.observe(this, this)

        binding.loginButton.setOnClickListener {
            loginViewModel.startLogin(binding.loginInput.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val authCode = data?.getStringExtra(LoginWebViewActivity.RESULT_AUTHORIZATION_CODE)
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && !authCode.isNullOrEmpty()) {
            loginViewModel.authCode(authCode)
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.login, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigation_settings -> {
                startActivity(SettingsActivity.newIntent(this))
                return true
            }
            R.id.navigation_info -> {
                startActivity(AboutActivity.newIntent(this))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onChanged(loginModel: LoginModel?) {
        binding.loginInput.setText(loginModel?.input)

        if(loginModel == null) {
            return
        }

        when(loginModel.state) {
            LoginState.NO_ERROR -> binding.loginInputLayout.error = null
            LoginState.AUTH_ERROR -> binding.loginInputLayout.error = "auth error"
            LoginState.INVALID_DOMAIN -> binding.loginInputLayout.error = "invalid domain"
            LoginState.NETWORK_ERROR -> binding.loginInputLayout.error = "network error"
            LoginState.LOADING -> {

            }
            LoginState.SUCCESS -> {
                startActivityForResult(LoginWebViewActivity.newIntent(loginModel.domain!!, loginModel.clientId!!, loginModel.clientSecret!!, this), REQUEST_CODE)
            }
            LoginState.SUCCESS_FINAL -> {
                startActivity(Intent(this, MainActivity::class.java)) // TODO dont create intent here
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 14
    }

}
