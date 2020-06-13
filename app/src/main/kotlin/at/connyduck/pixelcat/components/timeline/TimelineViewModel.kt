package at.connyduck.pixelcat.components.timeline

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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val db: AppDatabase,
    fediverseApi: FediverseApi
) : ViewModel() {

    @OptIn(FlowPreview::class)
    @ExperimentalPagingApi
    val statusFlow = accountManager::activeAccount.asFlow()
        .flatMapConcat { activeAccount ->
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                remoteMediator = TimelineRemoteMediator(activeAccount?.id!!, fediverseApi, db),
                pagingSourceFactory = { db.statusDao().statuses(activeAccount.id) }
            ).flow
        }
        .cachedIn(viewModelScope)

    fun onFavorite(status: StatusEntity) {
        viewModelScope.launch {
            //  repository.onFavorite(status, accountManager.activeAccount()?.id!!)
        }
    }

    fun onMediaVisibilityChanged(status: StatusEntity) {
        viewModelScope.launch {
            db.statusDao().changeMediaVisibility(!status.mediaVisible, status.id, accountManager.activeAccount()?.id!!)
        }
    }
}
