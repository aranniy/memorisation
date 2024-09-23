package fr.irif.lingeswaran.memorisation.features.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fr.irif.lingeswaran.memorisation.data.RappelWorker
import fr.irif.lingeswaran.memorisation.data.TimeConfig
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ParametresViewModel(private val application: Application) : AndroidViewModel(application) {

    private val parameterDataStore = application.dataStore

    private val KEY_FOND = stringPreferencesKey("fond")

    private val KEY_TAILLE = intPreferencesKey("taille")

    private val KEY_TIMER = intPreferencesKey("timer")

    private val KEY_H = intPreferencesKey("hour")

    private val KEY_M = intPreferencesKey("minute")

    val fond = parameterDataStore.data.map { it[KEY_FOND] ?: "#FFFFFF" }

    val taille = parameterDataStore.data.map { it[KEY_TAILLE] ?: 14 }

    val timer = parameterDataStore.data.map { it[KEY_TIMER] ?: 10 }

    var switchState = mutableStateOf(false)

    val prefConfig = parameterDataStore.data.map {
        TimeConfig(
            it[KEY_H] ?: 8,
            it[KEY_M] ?: 0,
        )
    }

    fun save(config: TimeConfig) {
        viewModelScope.launch {
            parameterDataStore.edit {
                it[KEY_M] = config.min
                it[KEY_H] = config.hour
            }
        }
    }

    fun schedule(config: TimeConfig) {
        val wm = WorkManager.getInstance(application)
        wm.cancelAllWork()
        wm.enqueue(perRequest(config.hour, config.min))

    }

    private fun perRequest(h: Int, m: Int): PeriodicWorkRequest {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
        }

        if (target.before(now))
            target.add(Calendar.DAY_OF_YEAR, 1)
        val delta=target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<RappelWorker>(
            24,
            TimeUnit.HOURS
        ).setInitialDelay(delta, TimeUnit.MILLISECONDS).build()
        Log.d("Periodic", "request: $request")
        return request
    }

    fun changeFond(fond: String) {
        viewModelScope.launch {
            parameterDataStore.edit { it[KEY_FOND] = fond }
        }
    }

    fun changeTaille(taille: Int) {
        viewModelScope.launch {
            parameterDataStore.edit { it[KEY_TAILLE] = taille }
        }
    }

    fun changeTimer(timer: Int) {
        viewModelScope.launch {
            parameterDataStore.edit { it[KEY_TIMER] = timer }
        }
    }

    fun changeSwitchState() {
        switchState.value = !switchState.value
    }

    fun clearDataStore() {
        viewModelScope.launch {
            parameterDataStore.edit {
                it.clear()
            }
        }
    }

}