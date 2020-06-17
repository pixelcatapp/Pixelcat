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

import at.connyduck.pixelcat.PixelcatApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        AndroidInjectionModule::class,
        ActivityModule::class,
        ViewModelModule::class,
        ServiceModule::class
    ]
)
interface AppComponent : AndroidInjector<PixelcatApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: PixelcatApplication): Builder

        fun build(): AppComponent
    }

    override fun inject(app: PixelcatApplication)
}
