package fr.irif.lingeswaran.memorisation.features.ui

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.irif.lingeswaran.memorisation.data.MemorisationApplication
import fr.irif.lingeswaran.memorisation.data.Questions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GestionQuestionsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as MemorisationApplication).database.myDao()

    var idTheme = mutableIntStateOf(1)

    var questions = mutableStateOf<Flow<List<Questions>>?>(dao.getQuestionsForJeux(idTheme.intValue))

    var selected = mutableStateOf<Questions?>(null)

    var status = mutableStateOf("")

    var menu = mutableStateOf(false)

    var modifyQuestionAlert = mutableStateOf(false)
    var deleteQuestionAlert = mutableStateOf(false)


    fun changeStatus(input : String) {
        status.value = input
    }

    fun updateId(newIdTheme : Int) {
        idTheme.intValue = newIdTheme
        questions.value = dao.getQuestionsForJeux(idTheme.intValue)
    }

    fun changeSelected(question : Questions) {
        selected.value = question
        modifyQuestionAlert.value = !modifyQuestionAlert.value
    }

    fun modifyQuestion(id : Int, newStatus : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateQuestionStatus(id, newStatus)
            status.value = ""
        }
    }

    fun deleteQuestion(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteQuestion(id)
        }
        deleteQuestionAlert.value = false
    }

}