package com.pokergrind.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Query("SELECT * FROM answers ORDER BY answeredAtEpochMillis")
    suspend fun getAll(): List<AnswerEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(answer: AnswerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(answers: List<AnswerEntity>)

    @Query("DELETE FROM answers")
    suspend fun deleteAll()

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

    @Query(
        """
        SELECT
            spotId,
            COUNT(*) AS answerCount,
            SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) AS correctCount,
            CAST(AVG(responseTimeMillis) AS INTEGER) AS averageResponseTimeMillis
        FROM answers
        WHERE mode = :mode
        GROUP BY spotId
        """,
    )
    fun observeSpotStats(mode: String): Flow<List<SpotStatsRow>>

    @Query(
        """
        SELECT
            spotId,
            handNotation,
            COUNT(*) AS answerCount,
            SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) AS correctCount,
            CAST(AVG(responseTimeMillis) AS INTEGER) AS averageResponseTimeMillis
        FROM answers
        WHERE mode = :mode
        GROUP BY spotId, handNotation
        """,
    )
    fun observeHandStats(mode: String): Flow<List<HandStatsRow>>
}

data class SpotStatsRow(
    val spotId: String,
    val answerCount: Int,
    val correctCount: Int,
    val averageResponseTimeMillis: Long,
)

data class HandStatsRow(
    val spotId: String,
    val handNotation: String,
    val answerCount: Int,
    val correctCount: Int,
    val averageResponseTimeMillis: Long,
)
