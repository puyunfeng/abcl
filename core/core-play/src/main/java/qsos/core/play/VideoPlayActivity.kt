package qsos.core.play

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.play_activity_main.*
import org.song.videoplayer.IVideoPlayer
import org.song.videoplayer.PlayListener
import qsos.lib.base.base.BaseNormalActivity
import qsos.lib.base.routepath.PlayPath

/**
 * @author : 华清松
 * @description : 视屏播放主页
 */
@Route(group = PlayPath.GROUP, path = PlayPath.VIDEO_PREVIEW)
class VideoPlayActivity(override val layoutId: Int? = R.layout.play_activity_main)
    : BaseNormalActivity() {

    @Autowired(name = PlayPath.VIDEO_URL)
    @JvmField
    var videoUrl: String? = null
    @Autowired(name = PlayPath.VIDEO_NAME)
    @JvmField
    var videoName: String? = null
    @Autowired(name = PlayPath.VIDEO_PATH)
    @JvmField
    var videoPath: String? = null

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        if (TextUtils.isEmpty(videoUrl)) {
            if (!TextUtils.isEmpty(videoPath)) {
                play_main_vv.setUp(videoPath, videoName)
            } else {
                showToast("播放链接错误")
                finishThis()
                return
            }
        } else {
            play_main_vv.setUp(videoUrl, videoName)
        }

        // 设置监听
        play_main_vv.setPlayListener(object : PlayListener {
            override fun onEvent(what: Int, vararg extra: Int?) {
                // 播放事件 初始化完成/缓冲/出错/点击事件...
                when (what) {
                    IVideoPlayer.EVENT_ERROR -> showToast("播放失败")
                }
            }

            override fun onStatus(status: Int) {
                // 播放器的ui状态
                if (status == IVideoPlayer.STATE_AUTO_COMPLETE) {
                    // 播放完成退出全屏
                    play_main_vv.quitWindowFullscreen()
                }
            }

            override fun onMode(mode: Int) {
                //全屏/普通/浮窗...
            }

        })

        // 进入全屏的模式 0横屏 1竖屏 2传感器自动横竖屏 3根据视频比例自动确定横竖屏 -1什么都不做
        play_main_vv.enterFullMode = 2
        play_main_vv.play()
    }

    override fun getData() {
    }

    override fun onBackPressed() {
        if (play_main_vv.onBackPressed()) return
        super.onBackPressed()
    }

    // 记录退出时播放状态 回来的时候继续播放
    private var playFlag: Boolean = false
    // 记录销毁时的进度 回来继续该进度播放
    private var position: Int = 0

    private val handler = Handler()

    public override fun onResume() {
        super.onResume()
        if (playFlag) {
            play_main_vv.play()
        }
        handler.removeCallbacks(runnable)
        if (position > 0) {
            play_main_vv.seekTo(position)
            position = 0
        }
    }

    public override fun onPause() {
        super.onPause()
        if (play_main_vv.isSystemFloatMode) return
        // 暂停
        playFlag = play_main_vv.isPlaying
        play_main_vv.pause()
    }

    public override fun onStop() {
        super.onStop()
        if (play_main_vv.isSystemFloatMode) return
        // 进入后台不马上销毁,延时15秒
        handler.postDelayed(runnable, 1000 * 15)
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (play_main_vv.isSystemFloatMode) {
            play_main_vv.quitWindowFloat()
        }
        play_main_vv.release()
        handler.removeCallbacks(runnable)
    }

    private val runnable = Runnable {
        if (play_main_vv.currentState != IVideoPlayer.STATE_AUTO_COMPLETE) {
            position = play_main_vv.position
        }
        play_main_vv.release()
    }

}