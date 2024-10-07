package fr.irif.lingeswaran.memorisation.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = JeuxQuestions::class,
        parentColumns = ["id"],
        childColumns = ["idJeux"],
        onDelete = ForeignKey.CASCADE)]
    )

data class Statistiques(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idJeux: Int,
    val date: String,
    val score : Int = 0,
    )