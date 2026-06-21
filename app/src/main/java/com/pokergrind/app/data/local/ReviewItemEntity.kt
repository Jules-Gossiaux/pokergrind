package com.pokergrind.app.data.local

import androidx.room.Entity

@Entity(
    tableName = "review_items",
    primaryKeys = ["spotId", "handNotation"],
)
data class ReviewItemEntity(
    val spotId: String,
    val handNotation: String,
    val dueAtEpochMillis: Long,
    val intervalDays: Int,
    val stage: Int,
    val lapses: Int,
    val priorityBoost: Int,
    val lastReviewedAtEpochMillis: Long,
)
