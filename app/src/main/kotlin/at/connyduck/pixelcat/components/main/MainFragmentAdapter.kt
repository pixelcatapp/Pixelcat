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
