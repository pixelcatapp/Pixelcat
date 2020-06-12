package at.connyduck.pixelcat.components.bottomsheet.accountselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.components.util.extension.show
import at.connyduck.pixelcat.databinding.ItemAccountSelectionBinding
import at.connyduck.pixelcat.db.entitity.AccountEntity
import coil.api.load
import coil.transform.RoundedCornersTransformation

class AccountSelectionAdapter(
    private val accounts: List<AccountEntity>,
    private val onAccountSelected: (Long) -> Unit,
    private val onAddAccount: () -> Unit
) : RecyclerView.Adapter<AccountSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSelectionViewHolder {
        val binding =
            ItemAccountSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountSelectionViewHolder(binding)
    }

    override fun getItemCount() = accounts.size + 1

    override fun onBindViewHolder(holder: AccountSelectionViewHolder, position: Int) {
        val binding = holder.binding
        if (position == accounts.size) {
            binding.accountAvatar.load(R.drawable.ic_plus_background)
            binding.accountName.hide()
            binding.accountDisplayName.setText(R.string.action_add_new_account)
            binding.root.isSelected = false
            binding.root.setOnClickListener {
                onAddAccount()
            }
        } else {
            val account = accounts[position]
            binding.accountAvatar.load(account.profilePictureUrl) {
                transformations(RoundedCornersTransformation(25f))
            }
            binding.accountDisplayName.text = account.displayName
            binding.accountName.show()
            binding.accountName.text = account.fullName

            binding.root.isSelected = account.isActive
            binding.root.setOnClickListener {
                if (!account.isActive) {
                    onAccountSelected(account.id)
                }
            }
        }
    }

}


class AccountSelectionViewHolder(val binding: ItemAccountSelectionBinding) :
    RecyclerView.ViewHolder(binding.root)