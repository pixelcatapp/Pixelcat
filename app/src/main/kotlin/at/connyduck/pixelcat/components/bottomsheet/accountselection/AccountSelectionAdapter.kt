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

package at.connyduck.pixelcat.components.bottomsheet.accountselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.components.util.extension.show
import at.connyduck.pixelcat.databinding.ItemAccountSelectionBinding
import at.connyduck.pixelcat.db.entitity.AccountEntity
import coil.api.load
import coil.transform.RoundedCornersTransformation

class AccountSelectionAdapter(
    private val accounts: List<AccountEntity>,
    private val onAccountSelected: (Long) -> Unit,
    private val onAddAccount: () -> Unit
) : RecyclerView.Adapter<AccountSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSelectionViewHolder {
        val binding =
            ItemAccountSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountSelectionViewHolder(binding)
    }

    override fun getItemCount() = accounts.size + 1

    override fun onBindViewHolder(holder: AccountSelectionViewHolder, position: Int) {
        val binding = holder.binding
        if (position == accounts.size) {
            binding.accountAvatar.load(R.drawable.ic_plus_background)
            binding.accountName.hide()
            binding.accountDisplayName.setText(R.string.action_add_new_account)
            binding.root.isSelected = false
            binding.root.setOnClickListener {
                onAddAccount()
            }
        } else {
            val account = accounts[position]
            binding.accountAvatar.load(account.profilePictureUrl) {
                transformations(RoundedCornersTransformation(25f))
            }
            binding.accountDisplayName.text = account.displayName
            binding.accountName.show()
            binding.accountName.text = account.fullName

            binding.root.isSelected = account.isActive
            binding.root.setOnClickListener {
                if (!account.isActive) {
                    onAccountSelected(account.id)
                }
            }
        }
    }
}

class AccountSelectionViewHolder(val binding: ItemAccountSelectionBinding) :
    RecyclerView.ViewHolder(binding.root)
