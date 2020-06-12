package at.connyduck.pixelcat.components.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.components.util.extension.show
import at.connyduck.pixelcat.databinding.ItemProfileImageBinding
import at.connyduck.pixelcat.model.Attachment
import at.connyduck.pixelcat.model.Status
import coil.api.load

class ProfileImageAdapter(
    private val imageSizePx: Int
): PagedListAdapter<Status, ProfileImageViewHolder>(
    object: DiffUtil.ItemCallback<Status>() {
        override fun areItemsTheSame(old: Status, new: Status): Boolean {
            return false
        }
        override fun areContentsTheSame(old: Status, new: Status): Boolean {
            return true
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileImageViewHolder {
        val binding = ItemProfileImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.root.layoutParams = ViewGroup.LayoutParams(imageSizePx, imageSizePx)

        return ProfileImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileImageViewHolder, position: Int) {
        getItem(position)?.let { status ->

            holder.binding.profileImageView.load(status.attachments.firstOrNull()?.previewUrl)

            when {
                status.attachments.size > 1 -> {
                    holder.binding.profileImageIcon.show()
                    holder.binding.profileImageIcon.setImageResource(R.drawable.ic_multiple)
                }
                status.attachments.first().type == Attachment.Type.VIDEO -> {
                    holder.binding.profileImageIcon.show()
                    holder.binding.profileImageIcon.setImageResource(R.drawable.ic_play)
                }
                else -> {
                    holder.binding.profileImageIcon.hide()
                }
            }
        }
    }

}


class ProfileImageViewHolder(val binding: ItemProfileImageBinding): RecyclerView.ViewHolder(binding.root)