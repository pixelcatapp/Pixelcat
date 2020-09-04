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

package at.connyduck.pixelcat.components.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.getColorForAttr
import at.connyduck.pixelcat.databinding.ViewStatusBinding

class StatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val binding: ViewStatusBinding

    init {
        binding = ViewStatusBinding.inflate(LayoutInflater.from(context), this)
        gravity = Gravity.CENTER
        setBackgroundColor(context.getColorForAttr(R.attr.colorSurface))
        orientation = VERTICAL
    }

    fun setOnRetryListener(listener: (View) -> Unit) {
        binding.statusButton.setOnClickListener(listener)
    }

    fun showGeneralError() {
        binding.statusMessage.setText(R.string.status_general_error)
        binding.statusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_alert_triangle, 0, 0)
    }

    fun showNetworkError() {
        binding.statusMessage.setText(R.string.status_network_error)
        binding.statusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_wifi_off, 0, 0)
    }
}
