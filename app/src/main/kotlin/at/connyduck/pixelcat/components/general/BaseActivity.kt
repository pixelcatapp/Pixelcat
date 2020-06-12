package at.connyduck.pixelcat.components.general

import android.os.Bundle
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.settings.AppSettings
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        theme.applyStyle(appSettings.getAppColorStyle(), true)
        if (!appSettings.useSystemFont()) {
            theme.applyStyle(R.style.NunitoFont, true)
        }
    }
}
