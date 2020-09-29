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
    accountManager: AccountManager,
    private val db: AppDatabase,
    private val api: FediverseApi,
    private val useCases: TimelineUseCases
) : ViewModel() {

    @OptIn(FlowPreview::class)
    @ExperimentalPagingApi
    val statusFlow = accountManager::activeAccount.asFlow()
        .flatMapConcat { activeAccount ->
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                remoteMediator = TimelineRemoteMediator(activeAccount?.id!!, api, db),
                pagingSourceFactory = { db.statusDao().statuses(activeAccount.id) }
            ).flow
        }
        .cachedIn(viewModelScope)

    fun onFavorite(status: StatusEntity) {
        viewModelScope.launch {
            useCases.onFavorite(status)
        }
    }

    fun onBoost(status: StatusEntity) {
        viewModelScope.launch {
            useCases.onBoost(status)
        }
    }

    fun onMediaVisibilityChanged(status: StatusEntity) {
        viewModelScope.launch {
            useCases.onMediaVisibilityChanged(status)
        }
    }
}
