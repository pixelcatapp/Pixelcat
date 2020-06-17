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

package at.connyduck.pixelcat.components.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.databinding.ItemTimelineImageBinding
import at.connyduck.pixelcat.model.Attachment
import coil.api.load

class TimelineImageAdapter : RecyclerView.Adapter<TimelineImageViewHolder>() {

    var images: List<Attachment> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineImageViewHolder {
        val binding = ItemTimelineImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineImageViewHolder(binding)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: TimelineImageViewHolder, position: Int) {

        holder.binding.timelineImageView.load(images[position].previewUrl)
    }
}

class TimelineImageViewHolder(val binding: ItemTimelineImageBinding) : RecyclerView.ViewHolder(binding.root)
