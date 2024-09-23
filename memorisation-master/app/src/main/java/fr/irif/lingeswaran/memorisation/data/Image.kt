package fr.irif.lingeswaran.memorisation.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Questions::class,
        parentColumns = ["idQuestion"],
        childColumns = ["idQuestion"],
        onDelete = ForeignKey.CASCADE)],
    primaryKeys = ["idQuestion", "path"]
)
data class Image(
    val idQuestion : Int,
    val path : String,
)

