package at.connyduck.pixelcat.dagger

import at.connyduck.pixelcat.components.notifications.NotificationsFragment
import at.connyduck.pixelcat.components.profile.ProfileFragment
import at.connyduck.pixelcat.components.search.SearchFragment
import at.connyduck.pixelcat.components.timeline.TimelineFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun timelineFragment(): TimelineFragment

    @ContributesAndroidInjector
    abstract fun searchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun notificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    abstract fun profileFragment(): ProfileFragment

}