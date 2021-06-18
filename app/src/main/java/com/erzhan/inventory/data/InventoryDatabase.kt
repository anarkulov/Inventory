package com.erzhan.inventory.data

import android.content.Context
import androidx.room.*
import com.erzhan.inventory.data.Inventory.Entry.DATABASE_NAME

@Database(
    entities = [Inventory::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun getInventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var instance: InventoryDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            InventoryDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}