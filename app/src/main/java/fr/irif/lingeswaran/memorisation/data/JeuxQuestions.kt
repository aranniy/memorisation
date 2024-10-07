package fr.irif.lingeswaran.memorisation.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class JeuxQuestions(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nom: String,
    val currentQuestion : Int = 0,
    val scoreEnCours: Int = 0
)
