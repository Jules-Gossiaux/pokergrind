package com.pokergrind.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spot_unlocks")
data class SpotUnlockEntity(
    @PrimaryKey val spotId: String,
    val unlockedAtEpochMillis: Long,
)
