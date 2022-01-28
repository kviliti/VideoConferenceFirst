package com.example.videoconference

import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator

var player: MediaPlayer? = null
var vib: Vibrator? = null
fun startPlay(context: Context, type: String) {
    vib = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    try {
        val a = longArrayOf(500, 1000, 500, 100, 200)
        assert(vib != null)
        vib?.vibrate(a, 0)
        player = if (type == "in") {
            MediaPlayer.create(context, R.raw.a)
        } else {
            MediaPlayer.create(context, R.raw.call_a)
        }
        player?.start()
    } catch (ignored: Exception) {
    }
}

fun stopPlay() {
    try {
        vib?.cancel()
        if (player!!.isPlaying && player != null) {
            player!!.stop()
        }
    } catch (ignored: Exception) {
    }
}
