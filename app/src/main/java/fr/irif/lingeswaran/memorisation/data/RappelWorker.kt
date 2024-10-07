package fr.irif.lingeswaran.memorisation.data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import fr.irif.lingeswaran.memorisation.features.createNotif

class RappelWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        createNotif(context)
        return Result.success()
    }
}