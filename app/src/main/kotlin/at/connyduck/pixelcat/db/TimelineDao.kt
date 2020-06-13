package at.connyduck.pixelcat.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.connyduck.pixelcat.db.entitity.StatusEntity

@Dao
interface TimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(status: StatusEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(statuses: List<StatusEntity>)

    @Delete
    suspend fun delete(status: StatusEntity)

    @Query("SELECT * FROM StatusEntity WHERE accountId = :accountId ORDER BY LENGTH(id) DESC, id DESC")
    fun statuses(accountId: Long): PagingSource<Int, StatusEntity>

    @Query("DELETE FROM StatusEntity WHERE accountId = :accountId")
    suspend fun clearAll(accountId: Long)

    @Query("UPDATE StatusEntity SET mediaVisible = :visible WHERE id = :statusId AND accountId = :accountId")
    suspend fun changeMediaVisibility(visible: Boolean, statusId: String, accountId: Long)
}
