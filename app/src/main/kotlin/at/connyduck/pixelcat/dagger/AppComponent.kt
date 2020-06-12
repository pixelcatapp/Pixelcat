package at.connyduck.pixelcat.dagger

import at.connyduck.pixelcat.PixelcatApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    NetworkModule::class,
    AndroidInjectionModule::class,
    ActivityModule::class,
    ViewModelModule::class,
    ServiceModule::class
])
interface AppComponent : AndroidInjector<PixelcatApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: PixelcatApplication): Builder

        fun build(): AppComponent
    }

    override fun inject(app: PixelcatApplication)
}