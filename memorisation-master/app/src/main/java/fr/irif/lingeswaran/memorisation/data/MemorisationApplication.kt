package fr.irif.lingeswaran.memorisation.data

import android.app.Application

class MemorisationApplication : Application() {

    val database:MemoBD by lazy { MemoBD.getDataBase((this))}

}