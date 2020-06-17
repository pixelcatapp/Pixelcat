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

package at.connyduck.pixelcat.components.profile

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.model.Status
import at.connyduck.pixelcat.network.FediverseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileDataSourceFactory(
    private val api: FediverseApi,
    private val accountId: String?,
    private val accountManager: AccountManager,
    private val scope: CoroutineScope
) : DataSource.Factory<String, Status>() {

    override fun create(): DataSource<String, Status> {
        val source = ProfileImageDataSource(api, accountId, accountManager, scope)
        return source
    }
}

class ProfileImageDataSource(
    private val api: FediverseApi,
    private val accountId: String?,
    private val accountManager: AccountManager,
    private val scope: CoroutineScope
) : ItemKeyedDataSource<String, Status>() {
    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Status>
    ) {
        scope.launch(context = Dispatchers.IO) {
            val id = accountId ?: accountManager.activeAccount()?.accountId!!
            api.accountStatuses(
                id,
                limit = params.requestedLoadSize,
                onlyMedia = true,
                excludeReblogs = true
            ).fold(
                {
                    callback.onResult(it)
                },
                {
                }
            )
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Status>) {
        scope.launch(context = Dispatchers.IO) {
            val id = accountId ?: accountManager.activeAccount()?.accountId!!
            api.accountStatuses(
                id,
                maxId = params.key,
                limit = params.requestedLoadSize,
                onlyMedia = true,
                excludeReblogs = true
            ).fold(
                {
                    callback.onResult(it)
                },
                {
                }
            )
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Status>) {
        // we always load from top
    }

    override fun getKey(item: Status) = item.id
}
