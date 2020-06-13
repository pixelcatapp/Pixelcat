package at.connyduck.pixelcat.components.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.components.profile.ProfileActivity
import at.connyduck.pixelcat.components.util.extension.visible
import at.connyduck.pixelcat.databinding.ItemStatusBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import coil.api.load
import coil.transform.RoundedCornersTransformation
import java.text.SimpleDateFormat

interface TimeLineActionListener {
    fun onFavorite(post: StatusEntity)
    fun onBoost(post: StatusEntity)
    fun onReply(status: StatusEntity)
    fun onMediaVisibilityChanged(status: StatusEntity)
}

object TimelineDiffUtil : DiffUtil.ItemCallback<StatusEntity>() {
    override fun areItemsTheSame(oldItem: StatusEntity, newItem: StatusEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StatusEntity, newItem: StatusEntity): Boolean {
        return oldItem == newItem
    }
}

class TimelineListAdapter(
    private val listener: TimeLineActionListener
) : PagingDataAdapter<StatusEntity, TimelineViewHolder>(TimelineDiffUtil) {

    private val dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {

        getItem(position)?.let { status ->

            // TODO order the stuff here

            (holder.binding.postImages.adapter as TimelineImageAdapter).images = status.attachments
            holder.binding.postAvatar.load(status.account.avatar) {
                transformations(RoundedCornersTransformation(25f))
            }

            holder.binding.postAvatar.setOnClickListener {
                holder.binding.root.context.startActivity(ProfileActivity.newIntent(holder.binding.root.context, status.account.id))
            }

            holder.binding.postDisplayName.text = status.account.displayName
            holder.binding.postName.text = "@${status.account.username}"

            holder.binding.postLikeButton.isChecked = status.favourited

            holder.binding.postLikeButton.setEventListener { button, buttonState ->
                listener.onFavorite(status)
                true
            }

            holder.binding.postBoostButton.setEventListener { button, buttonState ->
                listener.onBoost(status)
                true
            }

            holder.binding.postReplyButton.setOnClickListener {
                listener.onReply(status)
            }

            holder.binding.postIndicator.visible = status.attachments.size > 1

            holder.binding.postImages.visible = status.attachments.isNotEmpty()

            holder.binding.postDescription.text = status.content.parseAsHtml().trim()

            holder.binding.postDate.text = dateTimeFormatter.format(status.createdAt)

            holder.binding.postSensitiveMediaOverlay.visible = status.attachments.isNotEmpty() && !status.mediaVisible

            holder.binding.postSensitiveMediaOverlay.setOnClickListener {
                listener.onMediaVisibilityChanged(status)
            }
        }
    }
}

class TimelineViewHolder(val binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.postImages.adapter = TimelineImageAdapter()

        binding.postIndicator.setViewPager(binding.postImages)
        (binding.postImages.adapter as TimelineImageAdapter).registerAdapterDataObserver(binding.postIndicator.adapterDataObserver)
        // val snapHelper = PagerSnapHelper()
        // snapHelper.attachToRecyclerView(binding.postImages)
    }
}
