package fr.irif.lingeswaran.memorisation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Questions::class, JeuxQuestions::class, Statistiques::class, Choix::class, Image::class], version = 4)
abstract class MemoBD : RoomDatabase() {

    abstract fun myDao() : MyDao

    companion object {
        @Volatile
        private var instance: MemoBD? = null
        fun getDataBase(c: Context): MemoBD {
            if (instance != null) return instance!!
            val db = Room.databaseBuilder(c.applicationContext, MemoBD::class.java, "memo")
                .fallbackToDestructiveMigration().build()
            instance = db
            return instance!!
        }
    }

}