/*
 * Copyright (C) 2020 Conny Duck
 *
 * This file is part of Pixelcat.
 *
 * Pixelcat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pixelcat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.connyduck.pixelcat.components.compose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.ActivityComposeBinding
import at.connyduck.pixelcat.util.viewBinding
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import javax.inject.Inject

class ComposeActivity : BaseActivity(), OnImageActionClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ComposeViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(ActivityComposeBinding::inflate)

    private val adapter = ComposeImageAdapter(this)

    private lateinit var visibilityBottomSheet: BottomSheetBehavior<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val top = insets.getInsets(systemBars()).top
            val toolbarParams = binding.composeAppBar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top
            WindowInsetsCompat.CONSUMED
        }

        if (viewModel.imageLiveData.value.isNullOrEmpty()) {
            viewModel.addImage(intent.getStringExtra(EXTRA_MEDIA_URI)!!)
        }

        binding.composeToolBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.composeImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.composeImages.adapter = adapter

        visibilityBottomSheet = BottomSheetBehavior.from(binding.composeVisibilityBottomSheet)
        visibilityBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        binding.composeShareButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.sendStatus(
                    caption = binding.composeCaptionInput.text?.toString().orEmpty(),
                    sensitive = binding.composeNsfwSwitch.isChecked
                )
                finish()
            }
        }

        binding.composeVisibilityButton.setOnClickListener {
            visibilityBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.composeVisibilityPublic.setOnClickListener {
            changeVisibility(VISIBILITY.PUBLIC)
        }
        binding.composeVisibilityUnlisted.setOnClickListener {
            changeVisibility(VISIBILITY.UNLISTED)
        }
        binding.composeVisibilityFollowers.setOnClickListener {
            changeVisibility(VISIBILITY.FOLLOWERS_ONLY)
        }

        viewModel.imageLiveData.observe(
            this
        ) {
            adapter.submitList(it)
        }

        viewModel.visibility.observe(
            this
        ) {
            val visibilityString = when (it) {
                VISIBILITY.PUBLIC -> R.string.compose_visibility_public
                VISIBILITY.UNLISTED -> R.string.compose_visibility_unlisted
                VISIBILITY.FOLLOWERS_ONLY -> R.string.compose_visibility_followers_only
            }

            binding.composeVisibilityButton.text =
                getString(R.string.compose_visibility, getString(visibilityString))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICK_MEDIA) {
            val returnValue =
                data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            viewModel.addImage(returnValue?.first()!!)
        }
    }

    private fun changeVisibility(visibility: VISIBILITY) {
        viewModel.setVisibility(visibility)
        visibilityBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onAddImage() {
        val options = Options.init()
            .setRequestCode(REQUEST_CODE_PICK_MEDIA)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)

        Pix.start(this, options)
    }

    companion object {
        private const val REQUEST_CODE_PICK_MEDIA = 123
        private const val EXTRA_MEDIA_URI = "MEDIA_URI"

        fun newIntent(context: Context, mediaUri: String): Intent {
            return Intent(context, ComposeActivity::class.java).apply {
                putExtra(EXTRA_MEDIA_URI, mediaUri)
            }
        }
    }
}
