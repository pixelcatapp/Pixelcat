package at.connyduck.pixelcat.components.timeline

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import at.connyduck.pixelcat.db.AppDatabase
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.db.entitity.toEntity
import at.connyduck.pixelcat.network.FediverseApi

@ExperimentalPagingApi
class TimelineRemoteMediator(
    private val accountId: Long,
    private val api: FediverseApi,
    private val db: AppDatabase
) : RemoteMediator<Int, StatusEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StatusEntity>
    ): MediatorResult {
        val apiCall = when (loadType) {
            LoadType.REFRESH -> {
                api.homeTimeline(limit = state.config.initialLoadSize)
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(true)
            }
            LoadType.APPEND -> {
                val maxId = state.pages.findLast { it.data.isNotEmpty() }?.data?.lastOrNull()?.id
                Log.w("TimelineRemoteMediator", "anchorPosition: ${state.anchorPosition} maxId: $maxId")
                api.homeTimeline(maxId = maxId, limit = state.config.pageSize)
            }
        }

        return apiCall.fold(
            { statusResult ->
                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        db.statusDao().clearAll(accountId)
                    }
                    db.statusDao().insertOrReplace(statusResult.map { it.toEntity(accountId) })
                }
                MediatorResult.Success(endOfPaginationReached = statusResult.isEmpty())
            },
            {
                MediatorResult.Error(it)
            }
        )
    }

    override suspend fun initialize() = InitializeAction.SKIP_INITIAL_REFRESH
}
