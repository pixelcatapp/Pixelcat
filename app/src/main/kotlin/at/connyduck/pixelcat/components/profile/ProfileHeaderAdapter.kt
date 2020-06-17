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
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.visible
import at.connyduck.pixelcat.databinding.ItemProfileHeaderBinding
import at.connyduck.pixelcat.model.Account
import at.connyduck.pixelcat.model.Relationship
import coil.api.load
import coil.transform.RoundedCornersTransformation
import java.text.NumberFormat

class ProfileHeaderAdapter : RecyclerView.Adapter<ProfileHeaderViewHolder>() {

    private var account: Account? = null
    private var isSelf: Boolean = false
    private var relationship: Relationship? = null

    fun setAccount(account: Account, isSelf: Boolean) {
        this.account = account
        this.isSelf = isSelf
        notifyItemChanged(0, ACCOUNT_CHANGED)
    }

    fun setRelationship(relationship: Relationship) {
        this.relationship = relationship
        notifyItemChanged(1, RELATIONSHIP_CHANGED)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHeaderViewHolder {
        val binding = ItemProfileHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileHeaderViewHolder, position: Int) {
        // nothing to do
    }

    override fun onBindViewHolder(holder: ProfileHeaderViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty() || payloads.contains(ACCOUNT_CHANGED)) {
            account?.let {
                holder.binding.profileName.text = it.username

                holder.binding.profileFollowButton.visible = !isSelf
                holder.binding.profileMessageButton.visible = !isSelf

                val numberFormat = NumberFormat.getNumberInstance()

                holder.binding.profileImage.load(it.avatar) {
                    transformations(RoundedCornersTransformation(20f))
                }

                holder.binding.profileFollowersTextView.text = numberFormat.format(it.followersCount)
                holder.binding.profileFollowingTextView.text = numberFormat.format(it.followingCount)
                holder.binding.profileStatusesTextView.text = numberFormat.format(it.statusesCount)

                holder.binding.profileNote.text = it.note
            }
        }
        if (payloads.isEmpty() || payloads.contains(RELATIONSHIP_CHANGED)) {
            relationship?.let {
                if (it.following) {
                    holder.binding.profileFollowButton.setText(R.string.profile_follows_you)
                } else {
                    holder.binding.profileFollowButton.setText(R.string.profile_action_follow)
                }
                holder.binding.profileFollowsYouText.visible = it.followedBy
            }
        }
    }

    override fun getItemCount() = 1

    companion object {
        const val ACCOUNT_CHANGED = "ACCOUNT"
        const val RELATIONSHIP_CHANGED = "RELATIONSHIP"
    }
}

class ProfileHeaderViewHolder(val binding: ItemProfileHeaderBinding) : RecyclerView.ViewHolder(binding.root)
