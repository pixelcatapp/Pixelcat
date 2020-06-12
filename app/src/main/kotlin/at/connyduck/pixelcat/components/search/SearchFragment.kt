package at.connyduck.pixelcat.components.search

import androidx.fragment.app.viewModels
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.dagger.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SearchFragment: DaggerFragment(R.layout.fragment_search) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val searchViewModel: SearchViewModel by viewModels { viewModelFactory }

    companion object {
        fun newInstance() = SearchFragment()
    }

}