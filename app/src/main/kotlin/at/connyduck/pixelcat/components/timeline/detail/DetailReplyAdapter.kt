package at.connyduck.pixelcat.components.timeline.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.ListAdapter
import at.connyduck.pixelcat.components.timeline.TimeLineActionListener
import at.connyduck.pixelcat.components.timeline.TimelineDiffUtil
import at.connyduck.pixelcat.components.util.BindingHolder
import at.connyduck.pixelcat.databinding.ItemReplyBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import coil.api.load
import coil.transform.RoundedCornersTransformation
import java.text.SimpleDateFormat

class DetailReplyAdapter(
    private val listener: TimeLineActionListener
) : ListAdapter<StatusEntity, BindingHolder<ItemReplyBinding>>(TimelineDiffUtil) {

    private val dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingHolder<ItemReplyBinding> {
        val binding = ItemReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindingHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingHolder<ItemReplyBinding>, position: Int) {
        getItem(position)?.let { status ->

            holder.binding.postAvatar.load(status.account.avatar) {
                transformations(RoundedCornersTransformation(25f))
            }

            holder.binding.postDisplayName.text = status.account.displayName
            holder.binding.postName.text = "@${status.account.username}"

            holder.binding.postDescription.text = status.content.parseAsHtml().trim()

            holder.binding.postDate.text = dateTimeFormatter.format(status.createdAt)

            holder.binding.postLikeButton.isChecked = status.favourited

            holder.binding.postLikeButton.setEventListener { _, _ ->
                listener.onFavorite(status)
                true
            }

            holder.binding.postReplyButton.setOnClickListener {
                listener.onReply(status)
            }
        }
    }
}
