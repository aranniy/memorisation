package fr.irif.lingeswaran.memorisation.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Questions::class,
        parentColumns = ["idQuestion"],
        childColumns = ["idQuestion"],
        onDelete = ForeignKey.CASCADE)],
    primaryKeys = ["idQuestion", "choix"]
)
data class Choix(
    val idQuestion : Int,
    val choix : String,
    val isPath : Boolean = false
)

