package at.connyduck.pixelcat.dagger

import at.connyduck.pixelcat.components.compose.SendStatusService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun contributesSendStatusService(): SendStatusService


}
