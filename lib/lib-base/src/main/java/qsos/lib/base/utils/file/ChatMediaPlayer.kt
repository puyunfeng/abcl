package qsos.lib.base.utils.file

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * @description : 聊天语音消息播放工具类
 */
object ChatMediaPlayer {
    private val mediaPlayer = MediaPlayer()
    private lateinit var playerListener: PlayerListener

    /**是否已停止*/
    private var hasStop = false
    /**当前播放状态*/
    private var currentState = State.STOP
    private val handler = Handler(Looper.getMainLooper())

    interface PlayerListener {
        fun onPlayerStop()
    }

    /**初始化语音播放控制器*/
    fun init(context: Context): ChatMediaPlayer {
        PlayerModeManager.init(context)
        PlayerModeManager.isSpeakerOn()
        PlayerModeManager.isReceiver()
        PlayerModeManager.onPlay()
        PlayerModeManager.onStop()
        PlayerModeManager.setSpeakerOn(true)

        currentState = State.STOP
        return this
    }

    /**播放完毕监听*/
    fun listener(listener: PlayerListener): ChatMediaPlayer {
        playerListener = listener
        mediaPlayer.setOnPreparedListener {
            if (hasStop) {
                mediaPlayer.stop()
                currentState = State.STOP
                playerListener.onPlayerStop()
            } else {
                mediaPlayer.start()
                currentState = State.PLAYING
            }
        }
        mediaPlayer.setOnCompletionListener {
            currentState = State.STOP
            listener.onPlayerStop()
            PlayerModeManager.onStop()
        }
        mediaPlayer.setOnErrorListener { _, _, _ ->
            currentState = State.STOP
            PlayerModeManager.onStop()
            false
        }
        return this
    }

    /**播放音频文件*/
    fun play(build: PlayBuild) {
        if (TextUtils.isEmpty(build.value)) {
            return
        }
        if (currentState == State.PREPARING) {
            return
        }
        if (currentState == State.PLAYING) {
            mediaPlayer.stop()
        }
        currentState = State.PREPARING
        PlayerModeManager.onPlay()
        try {
            if (PlayerModeManager.isReceiver()) {
                // 听筒时延迟1S中播放
                handler.postDelayed({
                    startPlay(build)
                }, 1000)
            } else {
                startPlay(build)
            }
        } catch (e: Exception) {
            ToastUtils.showToast(build.context, "播放出错了 $e")
        }
    }

    private fun startPlay(build: PlayBuild) {
        mediaPlayer.reset()
        when (build.type) {
            ChatMediaPlayer.PlayType.PATH -> {
                /**播放本地音频文件*/
                mediaPlayer.setDataSource(build.value)
            }
            ChatMediaPlayer.PlayType.URL -> {
                /**播放网络音频文件*/
                mediaPlayer.setDataSource(build.context, Uri.parse(build.value))
            }
        }
        mediaPlayer.prepareAsync()
        hasStop = false
    }

    fun stop() {
        if (currentState == State.STOP) return
        if (currentState == State.PREPARING) {
            hasStop = true
        } else if (currentState == State.PLAYING) {
            mediaPlayer.stop()
        }
        playerListener.onPlayerStop()
        PlayerModeManager.onStop()
    }

    fun isPlaying(): Boolean {
        return currentState != State.STOP
    }

    fun destroy() {
        if (currentState == State.STOP) return
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    private enum class State {
        PREPARING,//准备中
        PLAYING,//播放中
        STOP//停止中
    }

    /**播放类型*/
    enum class PlayType {
        /**本地*/
        PATH,
        /**网络*/
        URL
    }

    /**
     * @param type 播放类型
     * @param value 播放路径或链接，根据类型判断*/
    data class PlayBuild(
            var context: Context,
            var type: PlayType,
            var value: String
    )


}