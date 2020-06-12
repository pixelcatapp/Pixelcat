package at.connyduck.pixelcat.components.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.databinding.ItemComposeImageBinding
import coil.api.load
import java.io.File

class ComposeImageAdapter : ListAdapter<String, ComposeImageViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(old: String, new: String): Boolean {
            return old == new
        }

        override fun areContentsTheSame(old: String, new: String): Boolean {
            return old == new
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeImageViewHolder {
        val binding =
            ItemComposeImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComposeImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComposeImageViewHolder, position: Int) {

        getItem(position)?.let { uri ->

            holder.binding.root.load(File(uri)) {
                placeholder(R.drawable.ic_cat)
                error(R.drawable.ic_message)
            }
        }
    }
}

class ComposeImageViewHolder(val binding: ItemComposeImageBinding) :
    RecyclerView.ViewHolder(binding.root)
