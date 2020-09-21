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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.db.AppDatabase
import at.connyduck.pixelcat.network.FediverseApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(
    accountManager: AccountManager,
    private val db: AppDatabase,
    private val fediverseApi: FediverseApi
) : ViewModel() {

    @OptIn(FlowPreview::class)
    @ExperimentalPagingApi
    val notificationsFlow = accountManager::activeAccount.asFlow()
        .flatMapConcat { activeAccount ->
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                remoteMediator = NotificationsRemoteMediator(activeAccount?.id!!, fediverseApi, db),
                pagingSourceFactory = { db.notificationsDao().notifications(activeAccount.id) }
            ).flow
        }
        .cachedIn(viewModelScope)
}
