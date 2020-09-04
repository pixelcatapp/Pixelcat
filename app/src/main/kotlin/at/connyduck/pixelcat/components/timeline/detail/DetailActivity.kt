package at.connyduck.pixelcat.components.timeline.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.recyclerview.widget.ConcatAdapter
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.components.timeline.TimeLineActionListener
import at.connyduck.pixelcat.components.util.Error
import at.connyduck.pixelcat.components.util.Loading
import at.connyduck.pixelcat.components.util.Success
import at.connyduck.pixelcat.components.util.extension.getDisplayWidthInPx
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.components.util.extension.show
import at.connyduck.pixelcat.components.util.getColorForAttr
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.ActivityDetailBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import javax.inject.Inject

class DetailActivity : BaseActivity(), TimeLineActionListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: DetailViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(ActivityDetailBinding::inflate)

    private lateinit var statusAdapter: DetailStatusAdapter

    private lateinit var repliesAdapter: DetailReplyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemInsets = insets.getInsets(systemBars())

            binding.root.setPadding(0, systemInsets.top, 0, 0)

            WindowInsetsCompat.Builder(insets)
                .setInsets(systemBars(), Insets.of(systemInsets.left, 0, systemInsets.right, systemInsets.bottom))
                .build()
        }

        binding.detailSwipeRefresh.setColorSchemeColors(
            getColorForAttr(R.attr.pixelcat_gradient_color_start),
            getColorForAttr(R.attr.pixelcat_gradient_color_end)
        )

        binding.detailSwipeRefresh.setOnRefreshListener {
            viewModel.reload(false)
        }

        binding.detailStatus.setOnRetryListener {
            viewModel.reload(true)
        }

        viewModel.setStatusId(intent.getStringExtra(EXTRA_STATUS_ID)!!)

        val displayWidth = getDisplayWidthInPx()

        statusAdapter = DetailStatusAdapter(displayWidth, this)
        repliesAdapter = DetailReplyAdapter(this)

        binding.detailRecyclerView.adapter = ConcatAdapter(statusAdapter, repliesAdapter)

        viewModel.currentStatus.observe(
            this,
            {
                when (it) {
                    is Success -> {
                        binding.detailSwipeRefresh.show()
                        binding.detailStatus.hide()
                        binding.detailProgress.hide()
                        binding.detailSwipeRefresh.isRefreshing = false
                        binding.detailRecyclerView.show()
                        statusAdapter.submitList(listOf(it.data))
                        it.data?.let { status ->
                            if (intent.getBooleanExtra(EXTRA_REPLY, false)) {
                                intent.removeExtra(EXTRA_REPLY)
                                onReply(status)
                            }
                        }
                    }
                    is Loading -> {
                        binding.detailSwipeRefresh.hide()
                        binding.detailStatus.hide()
                        binding.detailProgress.show()
                    }
                    is Error -> {
                        binding.detailSwipeRefresh.hide()
                        binding.detailStatus.show()
                        binding.detailProgress.hide()
                        binding.detailStatus.showGeneralError()
                    }
                }
            }
        )

        viewModel.replies.observe(
            this,
            {
                if (it is Success) {
                    repliesAdapter.submitList(it.data)
                }
            }
        )
    }

    override fun onFavorite(status: StatusEntity) {
        viewModel.onFavorite(status)
    }

    override fun onBoost(status: StatusEntity) {
        viewModel.onBoost(status)
    }

    override fun onReply(status: StatusEntity) {
        val replyBottomsheet = BottomSheetBehavior.from(binding.detailReplyBottomSheet)
        replyBottomsheet.state = BottomSheetBehavior.STATE_EXPANDED

        binding.detailReplyingTo.text = getString(R.string.status_details_replying_to, "@" + status.account.username)
        binding.detailReply.requestFocus()
        binding.detailReply.setText("@" + status.account.username + " ")
        binding.detailReply.setSelection(binding.detailReply.text?.length ?: 0)

        binding.detailReplyLayout.setEndIconOnClickListener {
            viewModel.onReply(status, binding.detailReply.text?.toString().orEmpty())
        }
    }

    override fun onMediaVisibilityChanged(status: StatusEntity) {
        viewModel.onMediaVisibilityChanged(status)
    }

    override fun onDetailsOpened(status: StatusEntity) {
        // nothing to do, we already are in details
    }

    companion object {
        private const val EXTRA_STATUS_ID = "STATUS_ID"
        private const val EXTRA_REPLY = "REPLY"

        fun newIntent(context: Context, statusId: String, reply: Boolean = false): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_STATUS_ID, statusId)
                putExtra(EXTRA_REPLY, reply)
            }
        }
    }
}
