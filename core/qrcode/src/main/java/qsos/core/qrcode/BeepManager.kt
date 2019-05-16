package qsos.core.qrcode

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Vibrator
import java.io.Closeable
import java.io.IOException

/**
 * @author : 华清松
 * @description : 响铃管理，用于声音提示
 */
internal class BeepManager(private val activity: Activity) : MediaPlayer.OnErrorListener, Closeable {
    private var mediaPlayer: MediaPlayer? = null
    private var playBeep: Boolean = false
    private var vibrate: Boolean = false

    init {
        this.mediaPlayer = null
        updatePrefs()
    }

    @Synchronized
    fun updatePrefs() {
        playBeep = shouldBeep(activity)
        vibrate = true
        if (playBeep && mediaPlayer == null) {
            activity.volumeControlStream = AudioManager.STREAM_MUSIC
            mediaPlayer = buildMediaPlayer(activity)
        }
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    fun playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer!!.start()
        }
        if (vibrate) {
            val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VIBRATE_DURATION)
        }
    }

    private fun buildMediaPlayer(activity: Context): MediaPlayer? {
        val mediaPlayer = MediaPlayer()
        try {
            val file = activity.resources.openRawResourceFd(R.raw.beep)
            file.use {
                mediaPlayer.setDataSource(it.fileDescriptor, it.startOffset, it.length)
            }
            mediaPlayer.setOnErrorListener(this)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.isLooping = false
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME)
            mediaPlayer.prepare()
            return mediaPlayer
        } catch (ioe: IOException) {
            mediaPlayer.release()
            return null
        }

    }

    @Synchronized
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            activity.finish()
        } else {
            close()
            updatePrefs()
        }
        return true
    }

    @Synchronized
    override fun close() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    companion object {

        private const val BEEP_VOLUME = 0.10f
        private const val VIBRATE_DURATION = 200L

        private fun shouldBeep(activity: Context): Boolean {
            var shouldPlayBeep = true
            val audioService = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
                shouldPlayBeep = false
            }
            return shouldPlayBeep
        }
    }

}
