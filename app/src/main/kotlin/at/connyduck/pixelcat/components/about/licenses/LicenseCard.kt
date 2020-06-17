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
import android.util.AttributeSet
import android.view.LayoutInflater
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.databinding.CardLicenseBinding
import com.google.android.material.card.MaterialCardView

class LicenseCard
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding =
        CardLicenseBinding.inflate(LayoutInflater.from(context), this)

    init {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LicenseCard, 0, 0)

        val name: String? = a.getString(R.styleable.LicenseCard_name)
        val license: String? = a.getString(R.styleable.LicenseCard_license)
        val link: String? = a.getString(R.styleable.LicenseCard_link)
        a.recycle()

        radius = resources.getDimension(R.dimen.license_card_radius)

        binding.licenseCardName.text = name
        binding.licenseCardLicense.text = license
        if (link.isNullOrBlank()) {
            binding.licenseCardLink.hide()
        } else {
            binding.licenseCardLink.text = link
            // setOnClickListener { LinkHelper.openLink(link, context) }
        }
    }
}
