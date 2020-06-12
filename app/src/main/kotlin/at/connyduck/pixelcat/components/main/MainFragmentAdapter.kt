package at.connyduck.pixelcat.components.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import at.connyduck.pixelcat.components.notifications.NotificationsFragment
import at.connyduck.pixelcat.components.profile.ProfileFragment
import at.connyduck.pixelcat.components.search.SearchFragment
import at.connyduck.pixelcat.components.timeline.TimelineFragment

class MainFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TimelineFragment.newInstance()
            1 -> SearchFragment.newInstance()
            2 -> NotificationsFragment.newInstance()
            3 -> ProfileFragment.newInstance()
            else -> {
                throw IllegalStateException()
            }
        }
    }

    override fun getItemCount() = 4
}
