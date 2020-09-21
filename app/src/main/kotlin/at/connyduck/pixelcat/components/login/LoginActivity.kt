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
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.lifecycle.lifecycleScope
import at.connyduck.pixelcat.components.main.MainActivity
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.about.AboutActivity
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.components.settings.SettingsActivity
import at.connyduck.pixelcat.components.util.extension.visible
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.ActivityLoginBinding
import at.connyduck.pixelcat.util.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class LoginActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(ActivityLoginBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val top = insets.getInsets(systemBars()).top
            val toolbarParams = binding.loginToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top
            WindowInsetsCompat.CONSUMED
        }

        setSupportActionBar(binding.loginToolbar)

        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
        }

        lifecycleScope.launch {
            viewModel.observe().collect { loginModel ->
                onChanged(loginModel)
            }
        }

        binding.loginButton.setOnClickListener {
            viewModel.startLogin(binding.loginInput.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val authCode = data?.getStringExtra(LoginWebViewActivity.RESULT_AUTHORIZATION_CODE)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && !authCode.isNullOrEmpty()) {
            viewModel.authCode(authCode)
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()

        if (!intent.hasExtra(LoginWebViewActivity.RESULT_AUTHORIZATION_CODE)) {
            viewModel.removeError()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.login, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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

    private fun onChanged(loginModel: LoginModel?) {

        binding.loginInput.setText(loginModel?.input)

        if (loginModel == null) {
            return
        }

        when (loginModel.state) {
            LoginState.NO_ERROR -> {
                binding.loginInputLayout.error = null
                setLoading(false)
            }
            LoginState.AUTH_ERROR -> {
                binding.loginInputLayout.error = "auth error"
                setLoading(false)
            }
            LoginState.INVALID_DOMAIN -> {
                binding.loginInputLayout.error = "invalid domain"
                setLoading(false)
            }
            LoginState.NETWORK_ERROR -> {
                binding.loginInputLayout.error = "network error"
                setLoading(false)
            }
            LoginState.LOADING -> {
                setLoading(true)
            }
            LoginState.SUCCESS -> {
                setLoading(true)
                startActivityForResult(LoginWebViewActivity.newIntent(loginModel.domain!!, loginModel.clientId!!, loginModel.clientSecret!!, this), REQUEST_CODE)
            }
            LoginState.SUCCESS_FINAL -> {
                setLoading(true)
                startActivity(MainActivity.newIntent(this))
                finish()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.loginLoading.visible = loading
        binding.loginImageView.visible = !loading
        binding.loginInputLayout.visible = !loading
        binding.loginButton.visible = !loading
    }

    companion object {
        private const val REQUEST_CODE = 14

        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
