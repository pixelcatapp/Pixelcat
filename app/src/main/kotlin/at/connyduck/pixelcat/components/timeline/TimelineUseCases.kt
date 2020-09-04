package at.connyduck.pixelcat.components.timeline

import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.db.AppDatabase
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.db.entitity.toEntity
import at.connyduck.pixelcat.model.Status
import at.connyduck.pixelcat.network.FediverseApi
import at.connyduck.pixelcat.network.calladapter.NetworkResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimelineUseCases @Inject constructor(
    val api: FediverseApi,
    val db: AppDatabase,
    val accountManager: AccountManager
) {

    suspend fun onFavorite(status: StatusEntity) {
        val alreadyFavourited = status.favourited
        if (alreadyFavourited) {
            api.unfavouriteStatus(status.actionableId)
        } else {
            api.favouriteStatus(status.actionableId)
        }.updateStatusInDb()
    }

    suspend fun onBoost(status: StatusEntity) {
        val alreadyBoosted = status.reblogged
        if (alreadyBoosted) {
            api.unreblogStatus(status.actionableId)
        } else {
            api.reblogStatus(status.actionableId)
        }.updateStatusInDb()
    }

    suspend fun onMediaVisibilityChanged(status: StatusEntity) {
        db.statusDao().changeMediaVisibility(
            !status.mediaVisible,
            status.id,
            accountManager.activeAccount()?.id!!
        )
    }

    private suspend fun NetworkResponse<Status>.updateStatusInDb() {
        fold<Any?>(
            { updatedStatus ->
                val accountId = accountManager.activeAccount()?.id!!
                val updatedStatusEntity = updatedStatus.toEntity(accountId)
                db.statusDao().insertOrReplace(updatedStatusEntity)
            },
            {
                // Todo
            }
        )
    }
}
