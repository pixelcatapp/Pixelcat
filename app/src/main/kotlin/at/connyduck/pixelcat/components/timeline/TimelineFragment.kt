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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.timeline.detail.DetailActivity
import at.connyduck.pixelcat.components.util.extension.getDisplayWidthInPx
import at.connyduck.pixelcat.components.util.getColorForAttr
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.FragmentTimelineBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.util.viewBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimelineFragment : DaggerFragment(R.layout.fragment_timeline), TimeLineActionListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TimelineViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentTimelineBinding::bind)

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.timelineSwipeRefresh.setColorSchemeColors(
            view.context.getColorForAttr(R.attr.pixelcat_gradient_color_start),
            view.context.getColorForAttr(R.attr.pixelcat_gradient_color_end)
        )

        binding.toolbar.setNavigationOnClickListener {
            binding.timelineRecyclerView.scrollToPosition(0)
        }

        val adapter = TimelineListAdapter(view.context.getDisplayWidthInPx(), this)

        binding.timelineRecyclerView.adapter = adapter
        (binding.timelineRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.timelineRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            viewModel.statusFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        binding.timelineSwipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        adapter.addDataRefreshListener {
            binding.timelineSwipeRefresh.isRefreshing = false
        }
    }

    override fun onFavorite(status: StatusEntity) {
        viewModel.onFavorite(status)
    }

    override fun onBoost(status: StatusEntity) {
        viewModel.onBoost(status)
    }

    override fun onReply(status: StatusEntity) {
        TODO("Not yet implemented")
    }

    override fun onMediaVisibilityChanged(status: StatusEntity) {
        viewModel.onMediaVisibilityChanged(status)
    }

    override fun onDetailsOpened(status: StatusEntity) {
        startActivity(DetailActivity.newIntent(requireContext(), status.actionableId))
    }

    companion object {
        fun newInstance() = TimelineFragment()
    }
}
