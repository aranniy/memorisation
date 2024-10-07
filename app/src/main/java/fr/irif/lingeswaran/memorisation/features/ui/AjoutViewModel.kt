package fr.irif.lingeswaran.memorisation.features.ui

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.irif.lingeswaran.memorisation.data.Choix
import fr.irif.lingeswaran.memorisation.data.MemorisationApplication
import fr.irif.lingeswaran.memorisation.data.Questions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AjoutViewModel(application: Application) : AndroidViewModel(application) {

    val nbrChoixOptions = listOf(2, 3, 4, 5, 6)

    var question = mutableStateOf("")
    var reponse = mutableStateOf("")
    var status = mutableStateOf("")
    var nbrChoix = mutableIntStateOf(nbrChoixOptions[0])
    var listeChoix = mutableStateListOf("", "", "", "", "", "")

    var ajout = mutableStateOf(false)

    val theme = mutableStateOf(1)

    var expanded = mutableStateOf(false)

    private val dao = (application as MemorisationApplication).database.myDao()

    fun onChangeChoix(saisie : String, index : Int) : String {
        listeChoix[index] = saisie
        return saisie
    }

    fun changeQuestion(saisie : String) {
        question.value = saisie
    }

    fun changeReponse(saisie : String) {
        reponse.value = saisie
    }

    fun changeStatus(saisie : String) {
        status.value = saisie
    }

    fun alertAjout()  {
        ajout.value = !ajout.value
    }

    fun addQuestion(isQCM : Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val question = question.value.trim()
        val reponse = reponse.value.trim()
        val status = if(status.value.toIntOrNull() == null) {
            0
        } else {
            status.value.toInt()
        }
        dao.insertQuestion(Questions(idJeux = theme.value, status = status, question = question, response = reponse, qcm = isQCM))
        if(!isQCM) reset()
    }

    fun addQCM() = viewModelScope.launch(Dispatchers.IO) {

        val addQuestionJob = viewModelScope.launch { addQuestion(true) }
        addQuestionJob.join()

        val id = viewModelScope.async { dao.getQuestionId(question.value) }.await()

        for(index in 0 until nbrChoix.value) {
            val choix = listeChoix[index].trim()
            if(id != 0) dao.insertChoix(Choix(id, choix))
        }

        reset()

    }

    private fun reset() {
        question = mutableStateOf("")
        reponse = mutableStateOf("")
        status = mutableStateOf("")
    }

}