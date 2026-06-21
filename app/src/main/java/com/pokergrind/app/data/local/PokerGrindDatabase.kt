package com.pokergrind.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AnswerEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class PokerGrindDatabase : RoomDatabase() {
    abstract fun answerDao(): AnswerDao

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
                    .build()
                    .also { instance = it }
            }
    }
}
