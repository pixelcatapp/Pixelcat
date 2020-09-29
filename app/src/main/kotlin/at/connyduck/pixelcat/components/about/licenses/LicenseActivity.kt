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

package at.connyduck.pixelcat.components.about.licenses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivityLicenseBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class LicenseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.licenseContainer) { _, insets ->
            val top = insets.getInsets(systemBars()).top
            val toolbarParams = binding.licenseToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top
            WindowInsetsCompat.CONSUMED
        }

        setSupportActionBar(binding.licenseToolbar)

        binding.licenseToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        loadFileIntoTextView(R.raw.apache, binding.licenseApacheTextView)
    }

    private fun loadFileIntoTextView(@RawRes fileId: Int, textView: TextView) {

        textView.text = buildString {
            BufferedReader(InputStreamReader(resources.openRawResource(fileId))).use { br ->
                var line: String? = br.readLine()
                while (line != null) {
                    append(line)
                    append('\n')
                    line = br.readLine()
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LicenseActivity::class.java)
    }
}
