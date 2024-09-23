package fr.irif.lingeswaran.memorisation.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    foreignKeys = [ForeignKey(
        entity = JeuxQuestions::class,
        parentColumns = ["id"],
        childColumns = ["idJeux"],
        onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["question"], unique = true)]
)

data class Questions(
    @PrimaryKey(autoGenerate = true)
    val idQuestion: Int = 0,
    val idJeux: Int,
    val question: String,
    val response: String,
    val status: Int,
    val qcm : Boolean = false,
    val image : Boolean = false
)
