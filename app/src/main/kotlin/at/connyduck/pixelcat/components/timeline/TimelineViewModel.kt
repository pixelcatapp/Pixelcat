package at.connyduck.pixelcat.components.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.db.AppDatabase
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.network.FediverseApi
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    // private val repository: TimelineRepo,
    private val accountManager: AccountManager,
    private val db: AppDatabase,
    fediverseApi: FediverseApi
) : ViewModel() {

    private val accountId = MutableLiveData<Long>()

    init {
        viewModelScope.launch {
            val currentAccountId = accountManager.activeAccount()?.id!!
            accountId.postValue(currentAccountId)
        }
    }

    @ExperimentalPagingApi
    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = TimelineRemoteMediator(0, fediverseApi, db),
        pagingSourceFactory = { db.statusDao().statuses(0) }
    ).flow.cachedIn(viewModelScope)

    fun onFavorite(status: StatusEntity) {
        viewModelScope.launch {
            //  repository.onFavorite(status, accountManager.activeAccount()?.id!!)
        }
    }
}
