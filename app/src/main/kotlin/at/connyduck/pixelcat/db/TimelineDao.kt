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
    suspend fun insertOrReplace(status: StatusEntity)

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
