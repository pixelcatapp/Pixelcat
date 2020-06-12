package at.connyduck.pixelcat.components.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivityProfileBinding

class ProfileActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            binding.root.setPadding(0, top, 0, 0)

            insets.consumeSystemWindowInsets()
        }

        if (supportFragmentManager.findFragmentById(R.id.layoutContainer) == null) {
            supportFragmentManager.commit {
                add(R.id.layoutContainer, ProfileFragment.newInstance(intent.getStringExtra(EXTRA_ACCOUNT_ID)))
            }
        }

    }

    companion object {
        private const val EXTRA_ACCOUNT_ID = "ACCOUNT_ID"

        fun newIntent(context: Context, accountId: String): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(EXTRA_ACCOUNT_ID, accountId)
            }
        }
    }
}