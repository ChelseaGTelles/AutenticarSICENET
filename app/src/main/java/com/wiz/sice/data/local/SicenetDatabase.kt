package com.wiz.sice.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wiz.sice.data.local.dao.*
import com.wiz.sice.data.local.entities.*

@Database(
    entities = [
        ProfileEntity::class,
        SicenetDataEntity::class,
        SessionEntity::class,
        CargaEntity::class,
        KardexEntity::class,
        CalifUnidadesEntity::class,
        CalifFinalesEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun sicenetDataDao(): SicenetDataDao
    abstract fun sessionDao(): SessionDao
    abstract fun cargaDao(): CargaDao
    abstract fun kardexDao(): KardexDao
    abstract fun califUnidadesDao(): CalifUnidadesDao
    abstract fun califFinalesDao(): CalifFinalesDao

    companion object {
        @Volatile
        private var INSTANCE: SicenetDatabase? = null

        fun getDatabase(context: Context): SicenetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SicenetDatabase::class.java,
                    "sicenet_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
