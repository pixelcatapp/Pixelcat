package at.connyduck.pixelcat.components.notifications

import androidx.fragment.app.viewModels
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.dagger.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class NotificationsFragment : DaggerFragment(R.layout.fragment_notifications) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val notificationsViewModel: NotificationsViewModel by viewModels { viewModelFactory }

    companion object {
        fun newInstance() = NotificationsFragment()
    }
}
