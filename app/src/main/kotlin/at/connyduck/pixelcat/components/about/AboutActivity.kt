package at.connyduck.pixelcat.components.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import at.connyduck.pixelcat.BuildConfig
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.about.licenses.LicenseActivity
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            val toolbarParams = binding.aboutToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top

            insets.consumeSystemWindowInsets()
        }

        setSupportActionBar(binding.aboutToolbar)

        binding.aboutToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.aboutAppVersion.text = getString(R.string.about_version, BuildConfig.VERSION_NAME)

        binding.aboutLicensesButton.setOnClickListener {
            startActivity(LicenseActivity.newIntent(this))
        }

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, AboutActivity::class.java)
    }
}