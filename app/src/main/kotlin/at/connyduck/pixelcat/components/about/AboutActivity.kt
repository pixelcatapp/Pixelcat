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

package at.connyduck.pixelcat.components.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import at.connyduck.pixelcat.BuildConfig
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.about.licenses.LicenseActivity
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            val toolbarParams = binding.aboutToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top

            insets.consumeSystemWindowInsets()
        }

        setSupportActionBar(binding.aboutToolbar)

        binding.aboutToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.aboutAppVersion.text = getString(R.string.about_version, BuildConfig.VERSION_NAME)

        binding.aboutLicensesButton.setOnClickListener {
            startActivity(LicenseActivity.newIntent(this))
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, AboutActivity::class.java)
    }
}
