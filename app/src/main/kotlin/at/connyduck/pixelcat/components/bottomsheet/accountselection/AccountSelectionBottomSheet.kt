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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import at.connyduck.pixelcat.components.login.LoginActivity
import at.connyduck.pixelcat.components.main.MainActivity
import at.connyduck.pixelcat.databinding.BottomsheetAccountsBinding
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class AccountSelectionBottomSheet(
    private val accountManager: AccountManager
) : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomsheetAccountsBinding::bind)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            binding.accountsRecyclerView.adapter =
                AccountSelectionAdapter(
                    accountManager.getAllAccounts(),
                    ::onAccountSelected,
                    ::onNewAccount
                )
        }
    }

    private fun onAccountSelected(accountId: Long) {
        lifecycleScope.launch {
            accountManager.setActiveAccount(accountId)
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            activity?.finish()
        }
    }

    private fun onNewAccount() {
        startActivity(LoginActivity.newIntent(requireContext()))
    }
}
