package fr.irif.lingeswaran.memorisation.features.ui

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.irif.lingeswaran.memorisation.data.MemorisationApplication
import fr.irif.lingeswaran.memorisation.data.Questions
import fr.irif.lingeswaran.memorisation.data.Statistiques
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as MemorisationApplication).database.myDao()


    var currentQuestionIndex =  mutableIntStateOf(0)
    var currentQuestion = mutableStateOf<Questions?>(null)
    var afficherReponse = mutableStateOf(false)
    var reponse = mutableStateOf("")
    var score = mutableIntStateOf(0)
    var erreur = mutableIntStateOf(0)


    var questions = mutableStateOf<Flow<List<Questions>>?>(dao.getQuestionsForJeux(1))
    var choix = mutableStateOf<Flow<List<String>>?>(null)

    var timeLeft = mutableIntStateOf(15)

    var start = mutableStateOf(false)
    var ready = mutableStateOf(false)
    var updated = mutableStateOf(false)


    var scoreEnCours = mutableStateOf(0)

    fun changeReponse(saisie : String) {
        reponse.value = saisie
    }

    fun setTimeLeft(i : Int) {
        this.timeLeft.value = i
    }
    fun incrementerCurrentQuestion() {
        this.currentQuestionIndex.value++
    }

    fun getScoreEnCours(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreEnCours.value = dao.getScoreEnCours(id)
        }
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }
    fun insertStatistique(idJeux: Int, score: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val date = getCurrentDate()
            val statistiques = Statistiques(idJeux=idJeux, date = date, score = score)
            dao.insertStatistique(statistiques)
        }
    }

    fun changeSelected() {
        this.afficherReponse.value = !afficherReponse.value
    }

    fun incrementerScore() {
        this.score.value++
    }

    fun decrementerScore() {
        this.score.value--
    }

    fun incrementerScoreJeux(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.incrementScoreEnCours(id)
        }
    }

    fun setScoreJeux(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.setScoreEnCours(id)
        }
    }

    fun clearField() {
        this.reponse.value = ""
    }

    fun changeTheme(id: Int) = viewModelScope.launch {
        questions.value = dao.getQuestionsForJeux(id)
        currentQuestion.value = dao.getCurrentQuestion(id)
        ready.value = true
    }

    fun updateCurrentQuestion(idTheme : Int, idQuestion : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateCurrentQuestion(idTheme, idQuestion)
        }
    }

    fun updateChoix(idQuestion : Int) = viewModelScope.launch(Dispatchers.IO) {
        choix.value = dao.getChoix(idQuestion)
        updated .value = true
    }

    fun changeCurrentQuestionIndex(index : Int) {
        currentQuestionIndex.value = index
    }

    fun changeStart() {
        start.value = !start.value
    }


}