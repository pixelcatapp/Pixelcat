

package at.connyduck.pixelcat.db

import android.util.Log
import at.connyduck.pixelcat.db.entitity.AccountAuthData
import at.connyduck.pixelcat.db.entitity.AccountEntity
import at.connyduck.pixelcat.model.Account

/**
 * This class caches the account database and handles all account related operations
 * @author ConnyDuck
 */

//TODO check if the comments are up to date

private const val TAG = "AccountManager"

class AccountManager(db: AppDatabase) {

    private var activeAccount: AccountEntity? = null

    private var accounts: MutableList<AccountEntity> = mutableListOf()
    private val accountDao: AccountDao = db.accountDao()


    suspend fun activeAccount(): AccountEntity? {
        if(activeAccount == null) {
            accounts = accountDao.loadAll().toMutableList()

            activeAccount = accounts.find { acc ->
                acc.isActive
            }
        }
        return activeAccount
    }


    /**
     * Adds a new empty account and makes it the active account.
     * More account information has to be added later with [updateActiveAccount]
     * or the account wont be saved to the database.
     * @param accessToken the access token for the new account
     * @param domain the domain of the accounts Mastodon instance
     */
    suspend fun addAccount(domain: String, authData: AccountAuthData) {

        activeAccount?.let {
            it.isActive = false
            Log.d(TAG, "addAccount: saving account with id " + it.id)

            accountDao.insertOrReplace(it)
        }

        val maxAccountId = accounts.maxBy { it.id }?.id ?: 0
        val newAccountId = maxAccountId + 1
        activeAccount = AccountEntity(
            id = newAccountId,
            domain = domain,
            auth = authData,
            isActive = true
        )

    }

    /**
     * Saves an already known account to the database.
     * New accounts must be created with [addAccount]
     * @param account the account to save
     */
    suspend fun saveAccount(account: AccountEntity) {
        if (account.id != 0L) {
            Log.d(TAG, "saveAccount: saving account with id " + account.id)
            accountDao.insertOrReplace(account)
        }

    }

    /**
     * Logs the current account out by deleting all data of the account.
     * @return the new active account, or null if no other account was found
     */
    suspend fun logActiveAccountOut(): AccountEntity? {

        if (activeAccount == null) {
            return null
        } else {
            accounts.remove(activeAccount!!)
            accountDao.delete(activeAccount!!)

            if (accounts.size > 0) {
                accounts[0].isActive = true
                activeAccount = accounts[0]
                Log.d(TAG, "logActiveAccountOut: saving account with id " + accounts[0].id)
                accountDao.insertOrReplace(accounts[0])
            } else {
                activeAccount = null
            }
            return activeAccount

        }

    }

    /**
     * updates the current account with new information from the mastodon api
     * and saves it in the database
     * @param account the [Account] object returned from the api
     */
    suspend fun updateActiveAccount(account: Account) {
        activeAccount?.let {
            it.accountId = account.id
            it.username = account.username
            it.displayName = account.name
            it.profilePictureUrl = account.avatar
          //  it.defaultPostPrivacy = account.source?.privacy ?: Status.Visibility.PUBLIC
            it.defaultMediaSensitivity = account.source?.sensitive ?: false
          //  it.emojis = account.emojis ?: emptyList()

            Log.d(TAG, "updateActiveAccount: saving account with id " + it.id)
            it.id = accountDao.insertOrReplace(it)

            val accountIndex = accounts.indexOf(it)

            if (accountIndex != -1) {
                //in case the user was already logged in with this account, remove the old information
                accounts.removeAt(accountIndex)
                accounts.add(accountIndex, it)
            } else {
                accounts.add(it)
            }

        }
    }

    /**
     * changes the active account
     * @param accountId the database id of the new active account
     */
    suspend fun setActiveAccount(accountId: Long) {

        activeAccount?.let {
            Log.d(TAG, "setActiveAccount: saving account with id " + it.id)
            it.isActive = false
            saveAccount(it)
        }

        activeAccount = accounts.find { acc ->
            acc.id == accountId
        }

        activeAccount?.let {
            it.isActive = true
            accountDao.insertOrReplace(it)
        }
    }

    /**
     * @return an immutable list of all accounts in the database with the active account first
     */
    suspend fun getAllAccounts(): List<AccountEntity> {

        // TODO only load accounts on demand
        accounts = accountDao.loadAll().toMutableList()

        activeAccount = accounts.find { acc ->
            acc.isActive
        }

        return accounts.toList()
    }

    /**
     * @return true if at least one account has notifications enabled
     */
    fun areNotificationsEnabled(): Boolean {
        return accounts.any { it.notificationsEnabled }
    }

    /**
     * Finds an account by its database id
     * @param accountId the id of the account
     * @return the requested account or null if it was not found
     */
    fun getAccountById(accountId: Long): AccountEntity? {
        return accounts.find { acc ->
            acc.id == accountId
        }
    }

}