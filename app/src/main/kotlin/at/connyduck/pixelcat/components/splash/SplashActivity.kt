package at.connyduck.pixelcat.components.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import at.connyduck.pixelcat.components.login.LoginActivity
import at.connyduck.pixelcat.components.main.MainActivity
import at.connyduck.pixelcat.db.AccountManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {

            val intent = if (accountManager.activeAccount() != null) {
                Intent(
                    this@SplashActivity,
                    MainActivity::class.java
                ) // TODO don't create intents here
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}
