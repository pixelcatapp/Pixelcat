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
