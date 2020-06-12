package at.connyduck.pixelcat.components.bottomsheet.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.connyduck.pixelcat.components.about.AboutActivity
import at.connyduck.pixelcat.components.settings.SettingsActivity
import at.connyduck.pixelcat.databinding.BottomsheetMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetMenuBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.menuSettings.setOnClickListener {
            startActivity(SettingsActivity.newIntent(requireActivity()))
            dismiss()
        }
        binding.menuAbout.setOnClickListener {
            startActivity(AboutActivity.newIntent(it.context))
            dismiss()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}