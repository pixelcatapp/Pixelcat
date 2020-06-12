package at.connyduck.pixelcat.components.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.databinding.ItemTimelineImageBinding
import at.connyduck.pixelcat.model.Attachment
import coil.api.load

class TimelineImageAdapter: RecyclerView.Adapter<TimelineImageViewHolder>() {

    var images: List<Attachment> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineImageViewHolder {
        val binding = ItemTimelineImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineImageViewHolder(binding)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: TimelineImageViewHolder, position: Int) {

        holder.binding.timelineImageView.load(images[position].previewUrl)

    }

}


class TimelineImageViewHolder(val binding: ItemTimelineImageBinding): RecyclerView.ViewHolder(binding.root)