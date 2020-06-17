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

package at.connyduck.pixelcat.components.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.network.FediverseApi
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val fediverseApi: FediverseApi,
    private val accountManager: AccountManager
) : ViewModel() {

    fun whatever() {
    }

    init {
        viewModelScope.launch {

            fediverseApi.accountVerifyCredentials().fold(
                { account ->
                    accountManager.updateActiveAccount(account)
                },
                {
                }
            )
        }
    }
}
