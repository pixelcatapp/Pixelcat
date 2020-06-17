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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.components.util.extension.show
import at.connyduck.pixelcat.databinding.ItemProfileImageBinding
import at.connyduck.pixelcat.model.Attachment
import at.connyduck.pixelcat.model.Status
import coil.api.load

class ProfileImageAdapter(
    private val imageSizePx: Int
) : PagingDataAdapter<Status, ProfileImageViewHolder>(
    object : DiffUtil.ItemCallback<Status>() {
        override fun areItemsTheSame(old: Status, new: Status): Boolean {
            return false
        }
        override fun areContentsTheSame(old: Status, new: Status): Boolean {
            return true
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileImageViewHolder {
        val binding = ItemProfileImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.root.layoutParams = ViewGroup.LayoutParams(imageSizePx, imageSizePx)

        return ProfileImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileImageViewHolder, position: Int) {
        getItem(position)?.let { status ->

            holder.binding.profileImageView.load(status.attachments.firstOrNull()?.previewUrl)

            when {
                status.attachments.size > 1 -> {
                    holder.binding.profileImageIcon.show()
                    holder.binding.profileImageIcon.setImageResource(R.drawable.ic_multiple)
                }
                status.attachments.first().type == Attachment.Type.VIDEO -> {
                    holder.binding.profileImageIcon.show()
                    holder.binding.profileImageIcon.setImageResource(R.drawable.ic_play)
                }
                else -> {
                    holder.binding.profileImageIcon.hide()
                }
            }
        }
    }
}

class ProfileImageViewHolder(val binding: ItemProfileImageBinding) : RecyclerView.ViewHolder(binding.root)
