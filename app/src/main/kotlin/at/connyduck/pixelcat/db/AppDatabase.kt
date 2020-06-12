package at.connyduck.pixelcat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import at.connyduck.pixelcat.db.entitity.AccountEntity
import at.connyduck.pixelcat.db.entitity.StatusEntity


@Database(entities = [AccountEntity::class, StatusEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun statusDao(): TimelineDao

}