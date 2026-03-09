package com.wiz.sice.data.local.dao

import android.database.Cursor
import androidx.room.*
import com.wiz.sice.data.local.entities.*

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile LIMIT 1")
    suspend fun getProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("DELETE FROM profile")
    suspend fun clearProfile()
}

@Dao
interface SicenetDataDao {
    @Query("SELECT * FROM sicenet_data WHERE dataType = :type")
    suspend fun getDataByType(type: String): SicenetDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: SicenetDataEntity)

    @Query("DELETE FROM sicenet_data")
    suspend fun clearAllData()
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM session WHERE id = 1")
    suspend fun getSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("DELETE FROM session")
    suspend fun clearSession()
}

@Dao
interface CargaDao {
    @Query("SELECT * FROM carga_academica ORDER BY materia")
    suspend fun getAllCarga(): List<CargaEntity>

    @Query("SELECT * FROM carga_academica")
    fun getAllCargaCursor(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CargaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: CargaEntity): Long

    @Query("DELETE FROM carga_academica")
    suspend fun clearCarga()

    @Query("DELETE FROM carga_academica WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("SELECT lastUpdated FROM carga_academica LIMIT 1")
    suspend fun getLastUpdated(): Long?

    @Query("SELECT * FROM carga_academica WHERE id = :id LIMIT 1")
    fun getById(id: Int): CargaEntity?
}

@Dao
interface KardexDao {
    @Query("SELECT * FROM kardex ORDER BY periodo DESC")
    suspend fun getAllKardex(): List<KardexEntity>

    @Query("SELECT * FROM kardex")
    fun getAllKardexCursor(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<KardexEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: KardexEntity): Long

    @Query("DELETE FROM kardex")
    suspend fun clearKardex()

    @Query("DELETE FROM kardex WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("SELECT lastUpdated FROM kardex LIMIT 1")
    suspend fun getLastUpdated(): Long?

    @Query("SELECT * FROM kardex WHERE id = :id LIMIT 1")
    fun getById(id: Int): KardexEntity?
}

@Dao
interface CalifUnidadesDao {
    @Query("SELECT * FROM calif_unidades ORDER BY materia")
    suspend fun getAllCalifUnidades(): List<CalifUnidadesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CalifUnidadesEntity>)

    @Query("DELETE FROM calif_unidades")
    suspend fun clearCalifUnidades()

    @Query("SELECT lastUpdated FROM calif_unidades LIMIT 1")
    suspend fun getLastUpdated(): Long?
}

@Dao
interface CalifFinalesDao {
    @Query("SELECT * FROM calif_finales ORDER BY materia")
    suspend fun getAllCalifFinales(): List<CalifFinalesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CalifFinalesEntity>)

    @Query("DELETE FROM calif_finales")
    suspend fun clearCalifFinales()

    @Query("SELECT lastUpdated FROM calif_finales LIMIT 1")
    suspend fun getLastUpdated(): Long?
}
