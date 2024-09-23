package fr.irif.lingeswaran.memorisation.features.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import fr.irif.lingeswaran.memorisation.data.MemorisationApplication
import fr.irif.lingeswaran.memorisation.data.JeuxQuestions

class ChoixViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as MemorisationApplication).database.myDao()

    var listjeuxquestions = dao.getAllJeuxQuestions()

    var selected = mutableStateOf<JeuxQuestions?>(null)

    fun changeSelected(c:JeuxQuestions) {
        selected.value = c
    }

}