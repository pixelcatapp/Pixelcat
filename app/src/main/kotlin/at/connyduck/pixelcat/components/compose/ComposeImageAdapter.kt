package at.connyduck.pixelcat.components.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.databinding.ItemComposeImageBinding
import at.connyduck.sparkbutton.helpers.Utils
import coil.api.load
import java.io.File

interface OnImageActionClickListener {
    fun onAddImage()
}

class ComposeImageAdapter(
    private val listener: OnImageActionClickListener
) : ListAdapter<String, ComposeImageViewHolder>(
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

            if (uri == ADD_ITEM) {
                holder.binding.root.load(R.drawable.ic_plus_square_large)
                holder.binding.root.setPadding(Utils.dpToPx(holder.binding.root.context, 40))
                holder.binding.root.setOnClickListener {
                    listener.onAddImage()
                }
                return
            } else {
                holder.binding.root.load(File(uri)) {
                    placeholder(R.drawable.ic_cat)
                    error(R.drawable.ic_message)
                }
                holder.binding.root.setOnClickListener {}
            }
        }
    }

    companion object {
        const val ADD_ITEM = "add_item"
    }
}

class ComposeImageViewHolder(val binding: ItemComposeImageBinding) :
    RecyclerView.ViewHolder(binding.root)
