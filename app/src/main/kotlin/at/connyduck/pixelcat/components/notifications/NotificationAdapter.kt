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

package at.connyduck.pixelcat.components.notifications

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.BindingHolder
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.databinding.ItemNotificationBinding
import at.connyduck.pixelcat.databinding.ItemNotificationFollowBinding
import at.connyduck.pixelcat.databinding.ItemReplyBinding
import at.connyduck.pixelcat.db.entitity.NotificationEntity
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.db.entitity.TimelineAccountEntity
import at.connyduck.pixelcat.model.Notification
import coil.load
import coil.transform.RoundedCornersTransformation
import java.text.DateFormat
import java.text.SimpleDateFormat

interface NotificationActionListener {
    fun onDetailsOpened(status: StatusEntity)
    fun onProfileOpened(account: TimelineAccountEntity)
}

object NotificationDiffUtil : DiffUtil.ItemCallback<NotificationEntity>() {
    override fun areItemsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
        return oldItem == newItem
    }
}

class NotificationAdapter(
    private val listener: NotificationActionListener
) : PagingDataAdapter<NotificationEntity, BindingHolder<*>>(NotificationDiffUtil) {

    private val dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<*> {
        val binding = when (viewType) {
            MENTION -> ItemReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FOLLOW -> ItemNotificationFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FAVOURITE -> ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            REBLOG -> ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            else -> throw IllegalStateException()
        }
        return BindingHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingHolder<*>, position: Int) {
        getItem(position)?.let { notification ->
            when (holder.binding) {
                is ItemReplyBinding -> holder.binding.bind(notification, dateTimeFormatter)
                is ItemNotificationFollowBinding -> holder.binding.bind(notification, listener)
                is ItemNotificationBinding -> holder.binding.bind(notification, listener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("NotificationAdapter", "$position ${getItem(position)}")
        return when (getItem(position)?.type) {
            Notification.Type.MENTION -> MENTION
            Notification.Type.REBLOG -> REBLOG
            Notification.Type.FAVOURITE -> FAVOURITE
            Notification.Type.FOLLOW -> FOLLOW
            else -> throw IllegalStateException()
        }
    }

    companion object {
        private const val MENTION = 1
        private const val FOLLOW = 2
        private const val FAVOURITE = 3
        private const val REBLOG = 4
    }
}

private fun ItemReplyBinding.bind(notification: NotificationEntity, dateTimeFormatter: DateFormat) {
    val status = notification.status!!

    postAvatar.load(status.account.avatar) {
        transformations(RoundedCornersTransformation(25f))
    }

    postDisplayName.text = status.account.displayName
    postName.text = "@${status.account.username}"

    postDescription.text = status.content.parseAsHtml().trim()

    postDate.text = dateTimeFormatter.format(status.createdAt)

    postLikeButton.hide()

    postReplyButton.hide()
}

private fun ItemNotificationFollowBinding.bind(notification: NotificationEntity, listener: NotificationActionListener) {

    val account = notification.account

    notificationText.text = notificationText.context.getString(R.string.notification_followed, notification.account.username)

    notificationAvatar.load(account.avatar) {
        transformations(RoundedCornersTransformation(25f))
    }
    notificationDisplayName.text = account.displayName
    notificationName.text = account.username

    root.setOnClickListener {
        listener.onProfileOpened(account)
    }
}

private fun ItemNotificationBinding.bind(notification: NotificationEntity, listener: NotificationActionListener) {

    notificationAvatar.load(notification.account.avatar) {
        transformations(RoundedCornersTransformation(25f))
    }

    if (notification.type == Notification.Type.REBLOG) {
        notificationText.text = notificationText.context.getString(R.string.notification_reblogged, notification.account.username)
    } else { // Notification.Type.FAVOURITE
        notificationText.text = notificationText.context.getString(R.string.notification_favourited, notification.account.username)
    }

    root.setOnClickListener {
        listener.onDetailsOpened(notification.status!!)
    }
}
