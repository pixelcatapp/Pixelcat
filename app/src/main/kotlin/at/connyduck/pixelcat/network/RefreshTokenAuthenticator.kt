package at.connyduck.pixelcat.network

import at.connyduck.pixelcat.db.AccountManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class RefreshTokenAuthenticator(private val accountManager: AccountManager) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {


        val currentAccount = runBlocking { accountManager.activeAccount() }


        // TODO

        return null
    }

}
