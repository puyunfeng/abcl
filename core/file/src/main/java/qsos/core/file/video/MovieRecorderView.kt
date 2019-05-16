package qsos.core.file.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.media.MediaRecorder
import android.media.MediaRecorder.OnErrorListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.LinearLayout
import qsos.core.file.R
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.file.FileUtils
import java.io.File
import java.io.IOException
import java.util.*

/**
 * @author : 华清松
 * @description : 视频 视图
 */
@SuppressLint("NewApi")
class MovieRecorderView constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
    : LinearLayout(context, attrs, defStyle), OnErrorListener, Camera.AutoFocusCallback {

    private var surfaceView: SurfaceView
    private var surfaceHolder: SurfaceHolder
    private var mediaRecorder: MediaRecorder? = null
    private var camera: Camera? = null
    // 计时器
    private var timer: Timer? = null
    // 视频录制分辨率宽度
    private var mWidth: Int = 0
    // 视频录制分辨率高度
    private var mHeight: Int = 0
    // 是否一开始就打开摄像头
    private var isOpenCamera: Boolean
    // 最长拍摄时间
    private var recordMaxTime: Int = 0
    /**获取当前录像时间*/
    var timeCount: Int = 0
    /**录像文件*/
    var recordFile: File? = null
    /*图片预览尺寸*/
    private var sizePicture: Int = 0
    /**录制完成监听*/
    private var onRecordFinishListener: OnRecordFinishListener? = null
    /**录制进度监听*/
    private var onRecordProgressListener: OnRecordProgressListener? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

    init {

        val a = context.obtainStyledAttributes(attrs,
                R.styleable.FileMovieRecorderView, defStyle, 0)

        // 默认640
        mWidth = a.getInteger(R.styleable.FileMovieRecorderView_file_record_width, 640)
        // 默认360
        mHeight = a.getInteger(R.styleable.FileMovieRecorderView_file_record_height, 360)
        // 默认打开摄像头
        isOpenCamera = a.getBoolean(R.styleable.FileMovieRecorderView_file_is_open_camera, true)
        // 默认最大拍摄时间为60s
        recordMaxTime = a.getInteger(R.styleable.FileMovieRecorderView_file_record_max_time, 60)

        LayoutInflater.from(context).inflate(R.layout.file_video_record, this)
        surfaceView = findViewById(R.id.surface_view)
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(CustomCallBack())

        a.recycle()
    }

    /**SurfaceHolder回调*/
    private inner class CustomCallBack : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            if (!isOpenCamera) return
            try {
                initCamera()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            if (!isOpenCamera) return
            freeCameraResource()
        }
    }

    /**初始化摄像头*/
    @Throws(IOException::class)
    fun initCamera() {
        if (camera != null) {
            freeCameraResource()
        }
        try {
            if (checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
            } else if (checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            freeCameraResource()
            (context as Activity).finish()
        }

        if (camera == null)
            return

        setCameraParams()
        camera!!.setDisplayOrientation(90)
        camera!!.setPreviewDisplay(surfaceHolder)
        camera!!.startPreview()
        camera!!.unlock()
    }

    /**检查是否有摄像头,facing前置还是后置*/
    private fun checkCameraFacing(facing: Int): Boolean {
        val cameraCount = Camera.getNumberOfCameras()
        val info = Camera.CameraInfo()
        for (i in 0 until cameraCount) {
            Camera.getCameraInfo(i, info)
            if (facing == info.facing) {
                return true
            }
        }
        return false
    }

    /**设置摄像头为竖屏*/
    private fun setCameraParams() {
        if (camera != null) {
            val params = camera!!.parameters
            params.set("orientation", "portrait")
            val supportedPictureSizes = params.supportedPictureSizes
            for (size in supportedPictureSizes) {
                sizePicture = if (size.height * size.width > sizePicture) size.height * size.width else sizePicture
            }
            setPreviewSize(params)
            params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            camera!!.parameters = params
        }
    }

    /**根据手机支持的视频分辨率，设置预览尺寸*/
    private fun setPreviewSize(params: Camera.Parameters) {
        if (camera == null) {
            return
        }
        // 获取手机支持的分辨率集合，并以宽度为基准降序排序
        val previewSizes = params.supportedPreviewSizes
        previewSizes.sortWith(Comparator { lhs, rhs -> Integer.compare(rhs.width, lhs.width) })

        var tmp: Float
        var minDiff = 100f
        val ratio = 3.0f / 4.0f
        var best: Camera.Size? = null
        for (s in previewSizes) {
            tmp = Math.abs(s.height.toFloat() / s.width.toFloat() - ratio)
            if (tmp < minDiff) {
                minDiff = tmp
                best = s
            }
        }

        params.setPreviewSize(best!!.width, best.height)
        // 预览比率
        if (params.supportedVideoSizes == null || params.supportedVideoSizes.size == 0) {
            mWidth = best.width
            mHeight = best.height
        } else {
            setVideoSize(params)
        }
    }

    /**根据手机支持的视频分辨率，设置录制尺寸*/
    private fun setVideoSize(params: Camera.Parameters) {
        if (camera == null) {
            return
        }
        // 获取手机支持的分辨率集合，并以宽度为基准降序排序
        val previewSizes = params.supportedVideoSizes
        previewSizes.sortWith(Comparator { lhs, rhs ->
            Integer.compare(rhs.width, lhs.width)
        })

        var tmp: Float
        var minDiff = 100f
        val ratio = 3.0f / 4.0f// 高宽比率3:4，且最接近屏幕宽度的分辨率
        var best: Camera.Size? = null
        for (s in previewSizes) {
            tmp = Math.abs(s.height.toFloat() / s.width.toFloat() - ratio)
            if (tmp < minDiff) {
                minDiff = tmp
                best = s
            }
        }
        // 设置录制尺寸
        mWidth = best!!.width
        mHeight = best.height
    }

    /**释放摄像头资源*/
    private fun freeCameraResource() {
        try {
            if (camera != null) {
                camera!!.setPreviewCallback(null)
                camera!!.stopPreview()
                camera!!.lock()
                camera!!.release()
                camera = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            camera = null
        }
    }

    /**开始录制视频，达到指定时间之后回调接口*/
    fun record(onRecordFinishListener: OnRecordFinishListener) {
        this.onRecordFinishListener = onRecordFinishListener
        try {
            recordFile = FileUtils.createMovieFile()
            // 如果未打开摄像头，则打开
            if (!isOpenCamera) initCamera()
            initRecord()
            // 时间计数器重新赋值
            timeCount = 0
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    // 进度监听
                    timeCount++
                    if (onRecordProgressListener != null) {
                        camera?.autoFocus(this@MovieRecorderView)
                        onRecordProgressListener!!.onProgressChanged(recordMaxTime, timeCount)
                    }
                    // 达到指定时间，停止拍摄
                    if (timeCount == recordMaxTime) {
                        stop()
                        this@MovieRecorderView.onRecordFinishListener?.onRecordFinish()
                    }
                }
            }, 0, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.showToast(context, "没有权限录制，请前往手机设置开启读取文件权限")
            if (mediaRecorder != null) {
                mediaRecorder?.release()
            }
            freeCameraResource()
        }

    }

    /**录制视频初始化*/
    @Throws(Exception::class)
    private fun initRecord() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.reset()
        if (camera != null) {
            mediaRecorder?.setCamera(camera)
        }
        mediaRecorder?.setOnErrorListener(this)
        mediaRecorder?.setPreviewDisplay(surfaceHolder.surface)
        // 视频源
        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        // 音频源
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        // 视频输出格式 也可设为3gp等其他格式
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        // 音频格式
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        // 设置分辨率
        mediaRecorder?.setVideoSize(mWidth, mHeight)
        // 设置清晰度
        when {
            sizePicture < 3000000 -> mediaRecorder?.setVideoEncodingBitRate(3 * 1920 * 1080)
            sizePicture in 3000000..5000000 -> mediaRecorder?.setVideoEncodingBitRate(5 * 1920 * 1080)
            sizePicture in 5000000..8000000 -> mediaRecorder?.setVideoEncodingBitRate(8 * 1920 * 1080)
            else -> mediaRecorder?.setVideoEncodingBitRate(1920 * 1080)
        }
        // 输出旋转90度，保持竖屏录制
        mediaRecorder?.setOrientationHint(90)
        // 设置录制的视频帧率
        mediaRecorder?.setVideoFrameRate(30)
        // 视频录制格式
        mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder?.setOutputFile(recordFile!!.absolutePath)
        mediaRecorder?.prepare()
        mediaRecorder?.start()
    }

    /**停止拍摄*/
    fun stop() {
        stopRecord()
        releaseRecord()
        freeCameraResource()
    }

    /**停止录制*/
    private fun stopRecord() {
        if (timer != null)
            timer!!.cancel()
        if (mediaRecorder != null) {
            // 设置后防止崩溃
            mediaRecorder?.setOnErrorListener(null)
            mediaRecorder?.setPreviewDisplay(null)
            try {
                mediaRecorder?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**释放资源*/
    private fun releaseRecord() {
        if (mediaRecorder != null) {
            mediaRecorder?.setOnErrorListener(null)
            try {
                mediaRecorder?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        mediaRecorder = null
    }

    /**设置最大录像时间*/
    fun setRecordMaxTime(recordMaxTime: Int) {
        this.recordMaxTime = recordMaxTime
    }

    /**录制完成接口*/
    interface OnRecordFinishListener {
        fun onRecordFinish()
    }

    /**设置录制进度监听*/
    fun setOnRecordProgressListener(onRecordProgressListener: OnRecordProgressListener) {
        this.onRecordProgressListener = onRecordProgressListener
    }

    /**录制进度接口*/
    interface OnRecordProgressListener {
        /**
         * 进度变化
         *
         * @param maxTime     最大时间，单位秒
         * @param currentTime 当前进度
         */
        fun onProgressChanged(maxTime: Int, currentTime: Int)
    }

    override fun onAutoFocus(success: Boolean, camera: Camera) {
        if (success) {

        } else {
            camera.autoFocus(this)
        }
    }

    override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
        try {
            mr?.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
