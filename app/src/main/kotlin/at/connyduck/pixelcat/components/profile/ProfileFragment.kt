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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.bottomsheet.accountselection.AccountSelectionBottomSheet
import at.connyduck.pixelcat.components.bottomsheet.menu.MenuBottomSheet
import at.connyduck.pixelcat.components.main.MainActivity
import at.connyduck.pixelcat.components.util.Success
import at.connyduck.pixelcat.components.util.extension.getDisplayWidthInPx
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.FragmentProfileBinding
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.model.Account
import at.connyduck.pixelcat.model.Relationship
import at.connyduck.pixelcat.util.arg
import at.connyduck.pixelcat.util.viewBinding
import at.connyduck.pixelcat.util.withArgs
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileFragment : DaggerFragment(R.layout.fragment_profile) {

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentProfileBinding::bind)

    private var loadedAccount: Account? = null

    private val headerAdapter = ProfileHeaderAdapter()
    private lateinit var imageAdapter: ProfileImageAdapter

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (activity is MainActivity) {
            binding.toolbar.inflateMenu(R.menu.secondary_navigation)
            binding.toolbar.setOnMenuItemClickListener {
                when (it.itemId) {

                    R.id.navigation_accounts -> {
                        val bottomSheetDialog =
                            AccountSelectionBottomSheet(accountManager)
                        bottomSheetDialog.show(childFragmentManager, "accountsBottomSheet")
                    }
                    R.id.navigation_menu -> {
                        val bottomSheetDialog =
                            MenuBottomSheet()
                        bottomSheetDialog.show(childFragmentManager, "menuBottomSheet")
                    }
                }
                true
            }
        } else {
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            binding.toolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        }

        val displayWidth = view.context.getDisplayWidthInPx()
        val imageSpacing = resources.getDimensionPixelOffset(R.dimen.profile_images_spacing)
        val imageSize = (displayWidth - (IMAGE_COLUMN_COUNT - 1) * imageSpacing) / IMAGE_COLUMN_COUNT
        imageAdapter = ProfileImageAdapter(imageSize)
        val layoutManager = GridLayoutManager(view.context, IMAGE_COLUMN_COUNT)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0) return IMAGE_COLUMN_COUNT
                return 1
            }
        }

        binding.profileRecyclerView.layoutManager = layoutManager

        binding.profileRecyclerView.adapter = ConcatAdapter(headerAdapter, imageAdapter)
        binding.profileRecyclerView.addItemDecoration(GridSpacingItemDecoration(IMAGE_COLUMN_COUNT, imageSpacing, 1))

        viewModel.setAccountInfo(arg(ACCOUNT_ID))

        viewModel.profile.observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is Success -> onAccountChanged(it.data)
                    is Error -> showError()
                }
            }
        )
        viewModel.relationship.observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is Success -> onRelationshipChanged(it.data)
                    is Error -> showError()
                }
            }
        )
        lifecycleScope.launch {
            viewModel.imageFlow.collectLatest { imageAdapter.submitData(it) }
        }
    }

    private fun onAccountChanged(account: Account?) {

        loadedAccount = account ?: return

        binding.toolbar.title = account.displayName

        headerAdapter.setAccount(account, viewModel.isSelf)
    }

    private fun onRelationshipChanged(relation: Relationship?) {
        relation?.let {
            headerAdapter.setRelationship(it)
        }
    }

    private fun showError() {
        Snackbar.make(binding.root, R.string.error_generic, Snackbar.LENGTH_LONG)
            .setAction(R.string.action_retry) { viewModel.load(reload = true) }
            .show()
    }

    companion object {
        private const val IMAGE_COLUMN_COUNT = 3
        private const val ACCOUNT_ID = "ACCOUNT_ID"

        /**
         * create a new ProfileFragment instance
         * @param accountId the id of the profile to load, null to show the profile of the currently active user
         */
        fun newInstance(accountId: String? = null) =
            ProfileFragment().withArgs { putString(ACCOUNT_ID, accountId) }
    }
}
