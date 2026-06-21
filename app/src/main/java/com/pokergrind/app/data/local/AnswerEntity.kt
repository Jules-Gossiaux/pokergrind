package com.pokergrind.app.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "answers",
    primaryKeys = ["sessionId", "questionIndex"],
    indices = [Index(value = ["spotId", "answeredAtEpochMillis"])],
)
data class AnswerEntity(
    val sessionId: String,
    val questionIndex: Int,
    val spotId: String,
    val handNotation: String,
    val expectedAction: String,
    val chosenAction: String,
    val isCorrect: Boolean,
    val responseTimeMillis: Long,
    val answeredAtEpochMillis: Long,
)
