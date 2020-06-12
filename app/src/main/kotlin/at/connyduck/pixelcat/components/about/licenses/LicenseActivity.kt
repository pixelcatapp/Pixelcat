package at.connyduck.pixelcat.components.about.licenses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RawRes
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivityLicenseBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class LicenseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.licenseContainer.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            val toolbarParams = binding.licenseToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top

            insets.consumeSystemWindowInsets()
        }

        setSupportActionBar(binding.licenseToolbar)

        binding.licenseToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        loadFileIntoTextView(R.raw.apache, binding.licenseApacheTextView)

    }

    private fun loadFileIntoTextView(@RawRes fileId: Int, textView: TextView) {

        val sb = StringBuilder()

        val br = BufferedReader(InputStreamReader(resources.openRawResource(fileId)))

        try {
            var line: String? = br.readLine()
            while (line != null) {
                sb.append(line)
                sb.append('\n')
                line = br.readLine()
            }
        } catch (e: IOException) {
            Log.w("LicenseActivity", e)
        }

        br.close()

        textView.text = sb.toString()

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LicenseActivity::class.java)
    }
}