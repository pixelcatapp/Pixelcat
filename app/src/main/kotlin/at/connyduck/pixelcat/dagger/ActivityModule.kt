package at.connyduck.pixelcat.dagger

import at.connyduck.pixelcat.components.main.MainActivity
import at.connyduck.pixelcat.components.about.AboutActivity
import at.connyduck.pixelcat.components.about.licenses.LicenseActivity
import at.connyduck.pixelcat.components.compose.ComposeActivity
import at.connyduck.pixelcat.components.login.LoginActivity
import at.connyduck.pixelcat.components.profile.ProfileActivity
import at.connyduck.pixelcat.components.settings.SettingsActivity
import at.connyduck.pixelcat.components.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    // TODO order stuff here
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributesLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector
    abstract fun contributesLicenseActivity(): LicenseActivity

    @ContributesAndroidInjector
    abstract fun contributesAboutActivity(): AboutActivity

    @ContributesAndroidInjector
    abstract fun contributesSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesProfileActivity(): ProfileActivity

    @ContributesAndroidInjector
    abstract fun contributesComposeActivity(): ComposeActivity
}
