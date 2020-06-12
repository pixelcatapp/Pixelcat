package at.connyduck.pixelcat.components.timeline

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.getColorForAttr
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.FragmentTimelineBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.util.viewBinding
import com.google.android.material.appbar.AppBarLayout
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimelineFragment: DaggerFragment(R.layout.fragment_timeline), TimeLineActionListener {

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

        val adapter = TimelineListAdapter(this)

        binding.timelineRecyclerView.adapter = adapter
        (binding.timelineRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.timelineRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        binding.timelineSwipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        adapter.addDataRefreshListener {
            binding.timelineSwipeRefresh.isRefreshing = false
        }


        //viewModel.posts.observe(viewLifecycleOwner, Observer { t -> adapter.submitList(t) })

    }

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onFavorite(post: StatusEntity) {
        viewModel.onFavorite(post)
    }

    override fun onBoost(post: StatusEntity) {
        TODO("Not yet implemented")
    }

    override fun onReply(status: StatusEntity) {
        TODO("Not yet implemented")
    }

}