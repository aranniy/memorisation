package fr.irif.lingeswaran.memorisation.features.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.irif.lingeswaran.memorisation.data.JeuxQuestions
import fr.irif.lingeswaran.memorisation.data.MemorisationApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GestionThemesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as MemorisationApplication).database.myDao()

    var themes = mutableStateOf(dao.getAllJeuxQuestions())

    var selected = mutableStateOf<JeuxQuestions?>(null)

    var saisie = mutableStateOf("")
    var name = mutableStateOf("")

    var modifyThemeAlert = mutableStateOf<Pair<Boolean, JeuxQuestions?>>(Pair(false, null))
    var addThemeAlert = mutableStateOf(false)
    var deleteThemeAlert = mutableStateOf(false)

    var menu = mutableStateOf(false)

    fun onSaisie(input : String) {
        saisie.value = input
        viewModelScope.launch(Dispatchers.IO) {
            if(saisie.value == "")
                themes.value = dao.getAllJeuxQuestions()
            else
                themes.value = dao.getJeuxQuestions(saisie.value)
        }
    }

    fun changeName(input : String) {
        name.value = input
    }

    fun addTheme() : Long {
        var result = (0).toLong()
        viewModelScope.launch(Dispatchers.IO) {
            val nom = name.value.trim()
            result = dao.insertJeuxQuestions(JeuxQuestions(nom = nom))
            name.value = ""
        }
        return result
    }

    fun modifyTheme(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.modifyTheme(id, name.value)
            name.value = ""
        }
        modifyThemeAlert.value = Pair(false, null)
    }

    fun deleteTheme(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTheme(id)
        }
        deleteThemeAlert.value = false
    }

}