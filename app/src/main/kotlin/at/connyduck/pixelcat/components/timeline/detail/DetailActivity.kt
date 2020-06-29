package at.connyduck.pixelcat.components.timeline.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.components.timeline.TimeLineActionListener
import at.connyduck.pixelcat.components.util.Success
import at.connyduck.pixelcat.components.util.extension.getDisplayWidthInPx
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.components.util.extension.show
import at.connyduck.pixelcat.components.util.getColorForAttr
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.ActivityDetailBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.util.viewBinding
import javax.inject.Inject

class DetailActivity: BaseActivity(), TimeLineActionListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: DetailViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(ActivityDetailBinding::inflate)

    private lateinit var statusAdapter: DetailStatusAdapter

    private lateinit var repliesAdapter: DetailReplyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            binding.root.setPadding(0, top, 0, 0)

            insets.consumeSystemWindowInsets()
        }

        binding.detailSwipeRefresh.setColorSchemeColors(
            getColorForAttr(R.attr.pixelcat_gradient_color_start),
            getColorForAttr(R.attr.pixelcat_gradient_color_end)
        )

        binding.detailSwipeRefresh.setOnRefreshListener {
            viewModel.reload(false)
        }

        viewModel.setStatusId(intent.getStringExtra(EXTRA_STATUS_ID)!!)

        val displayWidth = getDisplayWidthInPx()

        statusAdapter = DetailStatusAdapter(displayWidth, this)
        repliesAdapter = DetailReplyAdapter(this)

        binding.detailRecyclerView.adapter = ConcatAdapter(statusAdapter, repliesAdapter)

        viewModel.currentStatus.observe(this, Observer {
            if(it is Success) {
                binding.detailProgress.hide()
                binding.detailSwipeRefresh.isRefreshing = false
                binding.detailRecyclerView.show()
                statusAdapter.submitList(listOf(it.data))
            }
        })

        viewModel.replies.observe(this, Observer {
            if(it is Success) {
                repliesAdapter.submitList(it.data)
            }
        })

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
        // nothing to do, we already are in details
    }

    companion object {
        private const val EXTRA_STATUS_ID = "STATUS_ID"

        fun newIntent(context: Context, statusId: String): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_STATUS_ID, statusId)
            }
        }
    }

}