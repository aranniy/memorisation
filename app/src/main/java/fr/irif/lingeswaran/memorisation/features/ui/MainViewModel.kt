package fr.irif.lingeswaran.memorisation.features.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.irif.lingeswaran.memorisation.data.MemorisationApplication
import fr.irif.lingeswaran.memorisation.data.Statistiques
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val achievementDataStore = application.dataStore

    private val dao = (application as MemorisationApplication).database.myDao()

    var listjeuxquestions = dao.getAllJeuxQuestions()
    var liststatistique = mutableStateOf<Flow<List<Statistiques>>?>(dao.getStatistiques())

    private val KEY_TROPHEE1 = booleanPreferencesKey("trophee1")
    private val KEY_TROPHEE2 = booleanPreferencesKey("trophee2")
    private val KEY_TROPHEE3 = booleanPreferencesKey("trophee3")
    private val KEY_TROPHEE4 = booleanPreferencesKey("trophee4")
    private val KEY_TROPHEE5 = booleanPreferencesKey("trophee5")
    private val KEY_TROPHEE6 = booleanPreferencesKey("trophee6")

    val trophee1 = achievementDataStore.data.map { it[KEY_TROPHEE1] ?: false }
    val trophee2 = achievementDataStore.data.map { it[KEY_TROPHEE2] ?: false }
    val trophee3 = achievementDataStore.data.map { it[KEY_TROPHEE3] ?: false }
    val trophee4 = achievementDataStore.data.map { it[KEY_TROPHEE4] ?: false }
    val trophee5 = achievementDataStore.data.map { it[KEY_TROPHEE5] ?: false }
    val trophee6 = achievementDataStore.data.map { it[KEY_TROPHEE6] ?: false }

    fun validateTrophee(i: Int) {
        viewModelScope.launch {
            achievementDataStore.edit {
                when (i) {
                    1 -> it[KEY_TROPHEE1] = true
                    2 -> it[KEY_TROPHEE2] = true
                    3 -> it[KEY_TROPHEE3] = true
                    4 -> it[KEY_TROPHEE4] = true
                    5 -> it[KEY_TROPHEE5] = true
                    6 -> it[KEY_TROPHEE6] = true
                    else -> throw IllegalArgumentException("Trophée invalide index: $i")
                }
            }
        }
    }

    fun getStatistiques() {
        viewModelScope.launch(Dispatchers.IO) {
            liststatistique.value = dao.getStatistiques()
        }
    }


}