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

package at.connyduck.pixelcat.components.splash

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
                MainActivity.newIntent(this@SplashActivity)
            } else {
                LoginActivity.newIntent(this@SplashActivity)
            }
            startActivity(intent)
            finish()
        }
    }
}
