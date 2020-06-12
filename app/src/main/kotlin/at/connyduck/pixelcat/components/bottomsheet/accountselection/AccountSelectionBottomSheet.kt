package at.connyduck.pixelcat.components.bottomsheet.accountselection

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import at.connyduck.pixelcat.components.login.LoginActivity
import at.connyduck.pixelcat.components.main.MainActivity
import at.connyduck.pixelcat.databinding.BottomsheetAccountsBinding
import at.connyduck.pixelcat.db.AccountManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class AccountSelectionBottomSheet(
    private val accountManager: AccountManager
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAccountsBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            binding.accountsRecyclerView.adapter = AccountSelectionAdapter(accountManager.getAllAccounts(), ::onAccountSelected, ::onNewAccount)
        }
    }

    private fun onAccountSelected(accountId: Long) {
        lifecycleScope.launch {
            accountManager.setActiveAccount(accountId)
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            activity?.finish()
        }
    }

    private fun onNewAccount() {
        //TODO don't create intent here
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}