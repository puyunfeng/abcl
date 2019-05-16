package qsos.core.file.video

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.file_activity_take_video.*
import qsos.core.file.R
import qsos.lib.base.base.BaseNormalActivity
import qsos.lib.base.routepath.FilePath
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.file.FileUtils
import java.io.File
import java.io.IOException

/**
 * @author : 华清松
 * @description : 录制视频界面
 */
@SuppressLint("CheckResult")
@Route(group = FilePath.GROUP, path = FilePath.TAKE_VIDEO)
class TakeVideoActivity : BaseNormalActivity() {
    override val layoutId = R.layout.file_activity_take_video

    /**录制进度*/
    private val recordProgress = 100
    /**录制结束*/
    private val recordFinish = 101
    /**是否结束录制*/
    private var isFinish = true
    /**是否触摸在松开取消的状态*/
    private var isTouchOnUpToCancel = false
    /**当前进度*/
    private var mCurrentTime = 0

    /**是否拥有录制权限*/
    private var canRecord = false

    /**按下的位置*/
    private var startY: Float = 0f

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isFinish = true
        movieRecorderView.stop()
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView() {
        val dm = applicationContext.resources.displayMetrics
        val width = dm.widthPixels
        val layoutParams = movieRecorderView?.layoutParams as FrameLayout.LayoutParams
        layoutParams.height = width * 4 / 3
        // 根据屏幕宽度设置预览控件的尺寸
        movieRecorderView?.layoutParams = layoutParams
        val rlBottomRootLayoutParams = rl_bottom_root.layoutParams as FrameLayout.LayoutParams
        rlBottomRootLayoutParams.height = width / 3 * 2
        rl_bottom_root.layoutParams = rlBottomRootLayoutParams

        progressBar_loading.max = 10
        movieRecorderView?.setOnRecordProgressListener(object : MovieRecorderView.OnRecordProgressListener {
            override fun onProgressChanged(maxTime: Int, currentTime: Int) {
                mCurrentTime = currentTime
                handler.sendEmptyMessage(recordProgress)
            }
        })

        // 处理触摸事件
        button_shoot?.setOnTouchListener { _, event ->
            if (!canRecord) {
                return@setOnTouchListener true
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 提示上移取消
                    textView_up_to_cancel.visibility = View.VISIBLE
                    // 开始录制
                    isFinish = false
                    // 记录按下的坐标
                    startY = event.y
                    movieRecorderView.record(object : MovieRecorderView.OnRecordFinishListener {
                        override fun onRecordFinish() {
                            handler.sendEmptyMessage(recordFinish)
                        }
                    })
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    textView_up_to_cancel.visibility = View.GONE
                    textView_release_to_cancel.visibility = View.GONE
                    if (startY - event.y > 100) {
                        // 上移超过一定距离取消录制，删除文件
                        if (!isFinish) {
                            resetData()
                        }
                    } else {
                        if (movieRecorderView.timeCount > 1) {
                            // 录制时间超过1秒，录制完成
                            handler.sendEmptyMessage(recordFinish)
                        } else {
                            // 时间不足取消录制，删除文件
                            showToast("录制时间太短")
                            resetData()
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    // 根据触摸上移状态切换提示
                    if (startY - event.y > 100) {
                        //触摸在松开就取消的位置
                        isTouchOnUpToCancel = true
                        if (textView_up_to_cancel.visibility == View.VISIBLE) {
                            textView_up_to_cancel.visibility = View.GONE
                            textView_release_to_cancel.visibility = View.VISIBLE
                        }
                    } else {
                        // 触摸在正常录制的位置
                        isTouchOnUpToCancel = false
                        if (textView_up_to_cancel.visibility == View.GONE) {
                            textView_up_to_cancel.visibility = View.VISIBLE
                            textView_release_to_cancel.visibility = View.GONE
                        }
                    }
                }
            }
            return@setOnTouchListener true
        }

        button_restart.setOnClickListener {
            resetData()
        }

        button_sure.setOnClickListener {
            if (movieRecorderView.recordFile != null) {
                val file: File? = File(movieRecorderView.recordFile!!.absolutePath)
                if (file != null && file.exists()) {
                    val intent = Intent()
                    intent.putExtra("video_path", file.absolutePath)
                    setResult(FileUtils.TAKE_VIDEO_CODE, intent)
                    finish()
                }
            }
        }

        resetData()

        RxPermissions(this).request(Manifest.permission.CAMERA)!!.subscribe {
            canRecord = it
        }
    }

    override fun getData() {

    }

    override fun onResume() {
        super.onResume()
        if (isFinish) {
            movieRecorderView.stop()
        }
    }

    /**重置状态*/
    @SuppressLint("SetTextI18n")
    private fun resetData() {
        if (movieRecorderView.recordFile != null) {
            movieRecorderView.recordFile!!.delete()
        }
        movieRecorderView.stop()
        isFinish = true
        mCurrentTime = 0
        progressBar_loading.progress = 0
        textView_count_down.text = "00:00"
        button_shoot.isEnabled = true
        button_shoot.text = "按住拍"
        textView_up_to_cancel.visibility = View.GONE
        textView_release_to_cancel.visibility = View.GONE
        button_restart.visibility = View.GONE
        button_sure.visibility = View.GONE

        try {
            movieRecorderView.initCamera()
        } catch (e: IOException) {
            ToastUtils.showToast(this, "相机启动失败，请检查启动权限")
        }
    }

    @SuppressLint("SetTextI18n")
    private val handler = Handler { msg: Message ->
        when (msg.what) {
            recordProgress -> {
                progressBar_loading.progress = mCurrentTime
                if (mCurrentTime < 10) {
                    textView_count_down.text = "00:0$mCurrentTime"
                } else {
                    textView_count_down.text = "00:$mCurrentTime"
                }
            }
            recordFinish -> {
                if (isTouchOnUpToCancel) {
                    // 录制结束，还在上移删除状态没有松手，就复位录制
                    showToast("复位录制")
                    resetData()
                } else {
                    // 录制结束，在正常位置，录制完成跳转页面
                    isFinish = true
                    button_shoot.isEnabled = false
                    button_shoot.text = "已完成"
                    button_restart.visibility = View.VISIBLE
                    button_sure.visibility = View.VISIBLE

                    ToastUtils.showToast(this, "完成录制，已保存")
                    if (isFinish) {
                        movieRecorderView.stop()
                    }
                }
            }
            else -> {
            }
        }
        return@Handler true
    }
}