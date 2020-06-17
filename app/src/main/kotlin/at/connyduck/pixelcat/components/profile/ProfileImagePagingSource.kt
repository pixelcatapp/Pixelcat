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

import androidx.paging.PagingSource
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.model.Status
import at.connyduck.pixelcat.network.FediverseApi

class ProfileImagePagingSource(
    private val api: FediverseApi,
    private val accountId: String?,
    private val accountManager: AccountManager
) : PagingSource<String, Status>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Status> {

        if (params is LoadParams.Prepend) {
            // we only load from top
            return LoadResult.Page(data = emptyList(), nextKey = null, prevKey = null)
        }

        val id = accountId ?: accountManager.activeAccount()?.accountId!!

        return api.accountStatuses(
            id,
            maxId = params.key,
            limit = params.loadSize,
            onlyMedia = true,
            excludeReblogs = true
        ).fold(
            {
                LoadResult.Page(data = it, prevKey = null, nextKey = it.lastOrNull()?.id)
            },
            {
                LoadResult.Error(it)
            }
        )
    }
}
