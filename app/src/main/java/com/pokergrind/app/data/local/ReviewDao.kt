package com.pokergrind.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM review_items")
    suspend fun getAll(): List<ReviewItemEntity>

    @Query("SELECT * FROM review_items")
    fun observeAll(): Flow<List<ReviewItemEntity>>

    @Query("SELECT * FROM review_items WHERE spotId IN (:spotIds)")
    suspend fun getForSpots(spotIds: List<String>): List<ReviewItemEntity>

    @Query(
        """
        SELECT * FROM review_items
        WHERE spotId = :spotId AND handNotation = :handNotation
        LIMIT 1
        """,
    )
    suspend fun get(spotId: String, handNotation: String): ReviewItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ReviewItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ReviewItemEntity>)

    @Query("DELETE FROM review_items")
    suspend fun deleteAll()
}
