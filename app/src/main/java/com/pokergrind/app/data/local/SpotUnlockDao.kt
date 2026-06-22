package com.pokergrind.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpotUnlockDao {
    @Query("SELECT * FROM spot_unlocks ORDER BY unlockedAtEpochMillis")
    suspend fun getAll(): List<SpotUnlockEntity>

    @Query("SELECT spotId FROM spot_unlocks ORDER BY unlockedAtEpochMillis")
    fun observeUnlockedSpotIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(entity: SpotUnlockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockAll(entities: List<SpotUnlockEntity>)

    @Query("DELETE FROM spot_unlocks")
    suspend fun deleteAll()
}
