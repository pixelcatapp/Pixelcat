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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import at.connyduck.pixelcat.components.util.Error
import at.connyduck.pixelcat.components.util.Success
import at.connyduck.pixelcat.components.util.UiState
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.model.Account
import at.connyduck.pixelcat.model.Relationship
import at.connyduck.pixelcat.model.Status
import at.connyduck.pixelcat.network.FediverseApi
import com.bumptech.glide.util.Executors
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val fediverseApi: FediverseApi,
    private val accountManager: AccountManager
) : ViewModel() {

    val profile = MutableLiveData<UiState<Account>>()
    val relationship = MutableLiveData<UiState<Relationship>>()
    val profileImages = MutableLiveData<PagedList<Status>>()

    val isSelf: Boolean
        get() = accountId == null

    private var accountId: String? = null

    fun load(reload: Boolean = false) {
        loadAccount(reload)
        if (!isSelf) {
            loadRelationship(reload)
        }
        loadImages(reload)
    }

    fun setAccountInfo(accountId: String?) {
        this@ProfileViewModel.accountId = accountId
        load(false)
    }

    private fun loadAccount(reload: Boolean = false) {
        if (profile.value == null || reload) {
            viewModelScope.launch {
                fediverseApi.account(getAccountId()).fold(
                    {
                        profile.value = Success(it)
                    },
                    {
                        profile.value = Error(cause = it)
                    }
                )
            }
        }
    }

    private fun loadRelationship(reload: Boolean = false) {
        if (relationship.value == null || reload) {
            viewModelScope.launch {
                fediverseApi.relationships(listOf(getAccountId())).fold(
                    {
                        relationship.value = Success(it.first())
                    },
                    {
                        relationship.value = Error(cause = it)
                    }
                )
            }
        }
    }

    private fun loadImages(reload: Boolean = false) {
        if (profileImages.value == null || reload) {
            profileImages.value = PagedList.Builder(
                ProfileImageDataSource(
                    fediverseApi,
                    accountId,
                    accountManager,
                    viewModelScope
                ),
                20
            ).setNotifyExecutor(Executors.mainThreadExecutor())
                .setFetchExecutor(java.util.concurrent.Executors.newSingleThreadExecutor())
                .build()
        }
    }

    private suspend fun getAccountId(): String {
        return accountId ?: accountManager.activeAccount()?.accountId!!
    }
}
