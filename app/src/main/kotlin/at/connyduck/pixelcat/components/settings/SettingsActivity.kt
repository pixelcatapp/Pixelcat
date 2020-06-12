package at.connyduck.pixelcat.components.settings

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.databinding.ActivitySettingsBinding
import at.connyduck.pixelcat.util.viewBinding
import javax.inject.Inject

class SettingsActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var preferences: SharedPreferences

    private val binding by viewBinding(ActivitySettingsBinding::inflate)

    private var restartActivitiesOnExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.settingsContainer.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            val toolbarParams = binding.settingsToolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = top

            insets.consumeSystemWindowInsets()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        binding.settingsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        preferences.registerOnSharedPreferenceChangeListener(this)

        restartActivitiesOnExit = intent.getBooleanExtra(EXTRA_RESTART_ACTIVITIES, false)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when(key) {
            getString(R.string.key_pref_app_color) -> restartCurrentActivity()
            getString(R.string.key_pref_night_mode) -> AppCompatDelegate.setDefaultNightMode(appSettings.getNightMode())
        }
    }

    private fun restartCurrentActivity() {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(EXTRA_RESTART_ACTIVITIES, restartActivitiesOnExit)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackPressed() {
        val parentActivityName = intent.getStringExtra(EXTRA_PARENT_ACTIVITY)
        if(restartActivitiesOnExit && parentActivityName != null) {
            val restartIntent = Intent()
            restartIntent.component = ComponentName(this, intent.getStringExtra(EXTRA_PARENT_ACTIVITY)!!)
            restartIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(restartIntent)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)
        }
    }

    companion object {

        private const val EXTRA_PARENT_ACTIVITY = "parent"
        private const val EXTRA_RESTART_ACTIVITIES = "restart"

        fun newIntent(activity: Activity): Intent {
            return Intent(activity, SettingsActivity::class.java).apply {
                putExtra(EXTRA_PARENT_ACTIVITY, activity.componentName.className)
            }
        }
    }


}