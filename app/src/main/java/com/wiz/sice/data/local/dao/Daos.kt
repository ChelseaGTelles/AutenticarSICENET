package com.wiz.sice.data.local.dao

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
