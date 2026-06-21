package com.pokergrind.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(answer: AnswerEntity): Long

    @Query(
        """
        SELECT * FROM answers
        WHERE spotId = :spotId
          AND mode = 'GUIDED'
        ORDER BY answeredAtEpochMillis DESC
        LIMIT :limit
        """,
    )
    fun observeRecent(spotId: String, limit: Int): Flow<List<AnswerEntity>>

    @Query("SELECT COUNT(*) FROM answers WHERE mode = 'FREE'")
    fun observeFreeAnswerCount(): Flow<Int>
}
