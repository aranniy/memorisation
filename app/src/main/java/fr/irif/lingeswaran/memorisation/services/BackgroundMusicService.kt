package fr.irif.lingeswaran.memorisation.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import androidx.media3.common.MediaItem
import android.os.IBinder
import android.util.Log
import fr.irif.lingeswaran.memorisation.R
import java.lang.Thread.sleep

class BackgroundMusicService : Service() {

    private lateinit var player: MediaPlayer

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this, R.raw.bg)
        player.isLooping = true
        player.setVolume(100f, 100f)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null) sleep(1_000)
        player.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }


}