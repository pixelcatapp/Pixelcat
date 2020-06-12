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
): ViewModel() {

    fun whatever() {

    }

    init {
        viewModelScope.launch {

                fediverseApi.accountVerifyCredentials().fold({ account ->
                    accountManager.updateActiveAccount(account)
                }, {

                })
        }
    }

}