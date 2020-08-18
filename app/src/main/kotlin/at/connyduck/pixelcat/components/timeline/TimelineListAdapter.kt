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
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import at.connyduck.pixelcat.components.profile.ProfileActivity
import at.connyduck.pixelcat.components.util.BindingHolder
import at.connyduck.pixelcat.components.util.extension.visible
import at.connyduck.pixelcat.databinding.ItemStatusBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import coil.api.load
import coil.transform.RoundedCornersTransformation
import java.text.SimpleDateFormat

interface TimeLineActionListener {
    fun onFavorite(post: StatusEntity)
    fun onBoost(post: StatusEntity)
    fun onReply(status: StatusEntity)
    fun onMediaVisibilityChanged(status: StatusEntity)
}

object TimelineDiffUtil : DiffUtil.ItemCallback<StatusEntity>() {
    override fun areItemsTheSame(oldItem: StatusEntity, newItem: StatusEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StatusEntity, newItem: StatusEntity): Boolean {
        return oldItem == newItem
    }
}

class TimelineListAdapter(
    private val displayWidth: Int,
    private val listener: TimeLineActionListener
) : PagingDataAdapter<StatusEntity, BindingHolder<ItemStatusBinding>>(TimelineDiffUtil) {

    private val dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<ItemStatusBinding> {
        val binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.postImages.adapter = TimelineImageAdapter()
        binding.postIndicator.setViewPager(binding.postImages)
        (binding.postImages.adapter as TimelineImageAdapter).registerAdapterDataObserver(binding.postIndicator.adapterDataObserver)
        return BindingHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingHolder<ItemStatusBinding>, position: Int) {

        getItem(position)?.let { status ->

            // TODO order the stuff here

            (holder.binding.postImages.adapter as TimelineImageAdapter).images = status.attachments

            val maxImageRatio = status.attachments.map {
                if (it.meta?.small?.width == null || it.meta.small.height == null) {
                    1f
                } else {
                    it.meta.small.height.toFloat() / it.meta.small.width.toFloat()
                }
            }.maxOrNull()?.coerceAtMost(1f) ?: 1f

            holder.binding.postImages.layoutParams.height = (displayWidth * maxImageRatio).toInt()

            holder.binding.postAvatar.load(status.account.avatar) {
                transformations(RoundedCornersTransformation(25f))
            }

            holder.binding.postAvatar.setOnClickListener {
                holder.binding.root.context.startActivity(ProfileActivity.newIntent(holder.binding.root.context, status.account.id))
            }

            holder.binding.postDisplayName.text = status.account.displayName
            holder.binding.postName.text = "@${status.account.username}"

            holder.binding.postLikeButton.isChecked = status.favourited

            holder.binding.postLikeButton.setEventListener { _, _ ->
                listener.onFavorite(status)
                true
            }

            holder.binding.postBoostButton.isChecked = status.reblogged

            holder.binding.postBoostButton.setEventListener { _, _ ->
                listener.onBoost(status)
                true
            }

            holder.binding.postReplyButton.setOnClickListener {
                listener.onReply(status)
            }

            holder.binding.postIndicator.visible = status.attachments.size > 1

            holder.binding.postImages.visible = status.attachments.isNotEmpty()

            holder.binding.postDescription.text = status.content.parseAsHtml().trim()

            holder.binding.postDate.text = dateTimeFormatter.format(status.createdAt)

            holder.binding.postSensitiveMediaOverlay.visible = status.attachments.isNotEmpty() && !status.mediaVisible

            holder.binding.postSensitiveMediaOverlay.setOnClickListener {
                listener.onMediaVisibilityChanged(status)
            }
        }
    }
}
