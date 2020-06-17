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

package at.connyduck.pixelcat.components.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            binding.root.setPadding(0, top, 0, 0)

            insets.consumeSystemWindowInsets()
        }

        if (supportFragmentManager.findFragmentById(R.id.layoutContainer) == null) {
            supportFragmentManager.commit {
                add(R.id.layoutContainer, ProfileFragment.newInstance(intent.getStringExtra(EXTRA_ACCOUNT_ID)))
            }
        }
    }

    companion object {
        private const val EXTRA_ACCOUNT_ID = "ACCOUNT_ID"

        fun newIntent(context: Context, accountId: String): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(EXTRA_ACCOUNT_ID, accountId)
            }
        }
    }
}
