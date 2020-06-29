package at.connyduck.pixelcat.components.timeline.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import at.connyduck.pixelcat.components.timeline.TimeLineActionListener
import at.connyduck.pixelcat.components.timeline.TimelineDiffUtil
import at.connyduck.pixelcat.components.timeline.TimelineImageAdapter
import at.connyduck.pixelcat.components.timeline.bind
import at.connyduck.pixelcat.components.util.BindingHolder
import at.connyduck.pixelcat.databinding.ItemStatusBinding
import at.connyduck.pixelcat.db.entitity.StatusEntity
import java.text.SimpleDateFormat


class DetailStatusAdapter(
    private val displayWidth: Int,
    private val listener: TimeLineActionListener
): ListAdapter<StatusEntity, BindingHolder<ItemStatusBinding>>(TimelineDiffUtil) {

    private val dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingHolder<ItemStatusBinding> {
        val binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.postImages.adapter = TimelineImageAdapter()
        binding.postIndicator.setViewPager(binding.postImages)
        (binding.postImages.adapter as TimelineImageAdapter).registerAdapterDataObserver(binding.postIndicator.adapterDataObserver)
        return BindingHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingHolder<ItemStatusBinding>, position: Int) {
        getItem(position)?.let { status ->
            holder.bind(status, displayWidth, listener, dateTimeFormatter)
        }
    }

}
