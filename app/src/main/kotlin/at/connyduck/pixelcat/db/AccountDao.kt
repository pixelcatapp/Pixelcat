package at.connyduck.pixelcat.db

import androidx.room.*
import at.connyduck.pixelcat.db.entitity.AccountEntity

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(account: AccountEntity): Long

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM AccountEntity ORDER BY id ASC")
    suspend fun loadAll(): List<AccountEntity>

}
