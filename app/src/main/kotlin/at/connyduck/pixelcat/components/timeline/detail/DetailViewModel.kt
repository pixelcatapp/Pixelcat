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

package at.connyduck.pixelcat.components.timeline.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.connyduck.pixelcat.components.util.Loading
import at.connyduck.pixelcat.components.util.Success
import at.connyduck.pixelcat.components.util.UiState
import at.connyduck.pixelcat.components.util.Error
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.db.AppDatabase
import at.connyduck.pixelcat.db.entitity.StatusEntity
import at.connyduck.pixelcat.db.entitity.toEntity
import at.connyduck.pixelcat.network.FediverseApi
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    val api: FediverseApi,
    val db: AppDatabase,
    val accountManager: AccountManager
) : ViewModel() {

    val currentStatus = MutableLiveData<UiState<StatusEntity>>()
    val replies = MutableLiveData<UiState<List<StatusEntity>>>()

    private var statusId = ""

    fun setStatusId(statusId: String) {
        this.statusId = statusId

        currentStatus.value = Loading()
        replies.value = Loading()

        viewModelScope.launch {
            db.statusDao().status(statusId, accountManager.activeAccount()?.id!!)?.let {
                currentStatus.value = Success(it)
            }
            loadStatus()
        }
        viewModelScope.launch {
            loadReplies()
        }
    }

    fun reload() {
        currentStatus.value = Loading()
        replies.value = Loading()
        viewModelScope.launch {
            loadStatus()
        }
        viewModelScope.launch {
            loadReplies()
        }
    }


    private suspend fun loadStatus() {
        api.status(statusId).fold({
            val statusEntity = it.toEntity(accountManager.activeAccount()?.id!!)
            db.statusDao().insertOrReplace(statusEntity)
            currentStatus.value = Success(statusEntity)
        }, {
            currentStatus.value = Error(cause = it)
        })
    }

    private suspend fun loadReplies() {
        api.statusContext(statusId).fold({
            replies.value = Success(it.descendants.map{
                    descendant -> descendant.toEntity(accountManager.activeAccount()?.id!!)
            })
        }, {
            replies.value = Error(cause = it)
        })
    }

}
