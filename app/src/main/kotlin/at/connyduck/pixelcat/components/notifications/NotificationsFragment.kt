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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.profile.ProfileActivity
import at.connyduck.pixelcat.components.timeline.detail.DetailActivity
import at.connyduck.pixelcat.components.util.extension.getColorForAttr
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.FragmentNotificationsBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.db.entitity.TimelineAccountEntity
import at.connyduck.pixelcat.util.viewBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationsFragment :
    DaggerFragment(R.layout.fragment_notifications),
    NotificationActionListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: NotificationsViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentNotificationsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.notificationSwipeRefresh.setColorSchemeColors(
            view.context.getColorForAttr(R.attr.pixelcat_gradient_color_start),
            view.context.getColorForAttr(R.attr.pixelcat_gradient_color_end)
        )

        val adapter = NotificationAdapter(this)

        binding.notificationRecyclerView.adapter = adapter
        (binding.notificationRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.notificationRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            viewModel.notificationsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        binding.notificationSwipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        adapter.addLoadStateListener {
            if (it.refresh != LoadState.Loading) {
                binding.notificationSwipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onDetailsOpened(status: StatusEntity) {
        startActivity(DetailActivity.newIntent(requireContext(), status.id))
    }

    override fun onProfileOpened(account: TimelineAccountEntity) {
        startActivity(ProfileActivity.newIntent(requireContext(), account.id))
    }

    companion object {
        fun newInstance() = NotificationsFragment()
    }
}
