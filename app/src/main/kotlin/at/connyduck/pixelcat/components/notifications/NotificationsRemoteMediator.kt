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

package at.connyduck.pixelcat.components.notifications

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import at.connyduck.pixelcat.db.AppDatabase
import at.connyduck.pixelcat.db.entitity.NotificationEntity
import at.connyduck.pixelcat.db.entitity.toEntity
import at.connyduck.pixelcat.network.FediverseApi

@ExperimentalPagingApi
class NotificationsRemoteMediator(
    private val accountId: Long,
    private val api: FediverseApi,
    private val db: AppDatabase
) : RemoteMediator<Int, NotificationEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NotificationEntity>
    ): MediatorResult {
        val apiCall = when (loadType) {
            LoadType.REFRESH -> {
                api.notifications(limit = state.config.initialLoadSize, excludes = setOf("poll", "follow_request"))
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(true)
            }
            LoadType.APPEND -> {
                val maxId = state.pages.findLast { it.data.isNotEmpty() }?.data?.lastOrNull()?.id
                api.notifications(maxId = maxId, limit = state.config.pageSize, excludes = setOf("poll", "follow_request"))
            }
        }

        return apiCall.fold(
            { notificationResult ->
                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        db.notificationsDao().clearAll(accountId)
                    }
                    db.notificationsDao().insertOrReplace(notificationResult.map { it.toEntity(accountId) })
                }
                MediatorResult.Success(endOfPaginationReached = notificationResult.isEmpty())
            },
            {
                MediatorResult.Error(it)
            }
        )
    }

    override suspend fun initialize() = InitializeAction.SKIP_INITIAL_REFRESH
}
