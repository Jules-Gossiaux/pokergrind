package com.pokergrind.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        AnswerEntity::class,
        ReviewItemEntity::class,
        SpotUnlockEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class PokerGrindDatabase : RoomDatabase() {
    abstract fun answerDao(): AnswerDao
    abstract fun reviewDao(): ReviewDao
    abstract fun spotUnlockDao(): SpotUnlockDao

    companion object {
        @Volatile
        private var instance: PokerGrindDatabase? = null

        fun getInstance(context: Context): PokerGrindDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PokerGrindDatabase::class.java,
                    "pokergrind.db",
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE answers ADD COLUMN mode TEXT NOT NULL DEFAULT 'GUIDED'",
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS review_items (
                        spotId TEXT NOT NULL,
                        handNotation TEXT NOT NULL,
                        dueAtEpochMillis INTEGER NOT NULL,
                        intervalDays INTEGER NOT NULL,
                        stage INTEGER NOT NULL,
                        lapses INTEGER NOT NULL,
                        priorityBoost INTEGER NOT NULL,
                        lastReviewedAtEpochMillis INTEGER NOT NULL,
                        PRIMARY KEY(spotId, handNotation)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS spot_unlocks (
                        spotId TEXT NOT NULL,
                        unlockedAtEpochMillis INTEGER NOT NULL,
                        PRIMARY KEY(spotId)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO spot_unlocks(spotId, unlockedAtEpochMillis)
                    VALUES ('open_btn_100bb_v1', 0)
                    """.trimIndent(),
                )
            }
        }
    }
}
