package qsos.lib.base.utils.record

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import qsos.lib.base.utils.LogUtil

/**
 * @author : 华清松
 * @description : 音频录制工具类
 */
object AudioUtils {

    const val FLAG_WAV = 0
    const val FLAG_AMR = 1
    const val CMD_RECORDING_TIME = 2000
    const val CMD_RECORD_FAIL = 2001
    const val CMD_STOP = 2002

    var uiHandler: UIHandler? = null
    /**-1:没再录制，0：录制wav，1：录制amr*/
    private var mState = -1
    private var uiThread: UIThread? = null
    private var mSize: Long = 0L
    /**录音文件路径*/
    var audioPath: String = ""

    /**录制监听*/
    interface OnRecordListener {
        fun record(recordStatus: RecordStatus)
    }

    /**
     * 录制状态实体
     * @param time 录制时间
     * @param size 录制大小
     * @param path 录制路径
     * @param done 录制完成
     */
    data class RecordStatus(var time: Int, var size: Long, var path: String?, var done: Boolean)

    private var mRecordListener: OnRecordListener? = null
    /**
     * 开始录音
     * @param mFlag，0：录制 wav 格式，1：录音 amr 格式
     */
    fun record(mFlag: Int, recordListener: OnRecordListener?) {
        this.mRecordListener = recordListener
        if (uiHandler == null) {
            uiHandler = UIHandler()
        }
        if (mState != -1) {
            val msg = Message()
            val b = Bundle()
            b.putInt("cmd", CMD_RECORD_FAIL)
            b.putInt("msg", MediaRecordFunc.ErrorCode.E_STATE_RECODING)
            msg.data = b
            /**向Handler发送消息,更新UI*/
            uiHandler?.sendMessage(msg)
            return
        }
        var mResult = -1
        when (mFlag) {
            FLAG_WAV -> {
                audioPath = AudioFileFunc.wavFilePath
                mResult = AudioRecordFunc.startRecordAndFile()
            }
            FLAG_AMR -> {
                audioPath = AudioFileFunc.amrFilePath
                mResult = MediaRecordFunc.startRecordAndFile()
            }
        }
        if (mResult == MediaRecordFunc.ErrorCode.SUCCESS) {
            uiThread = UIThread()
            Thread(uiThread).start()
            mState = mFlag
        } else {
            val msg = Message()
            /**存放数据*/
            val b = Bundle()
            b.putInt("cmd", CMD_RECORD_FAIL)
            b.putInt("msg", mResult)
            msg.data = b

            // 向Handler发送消息,更新UI
            uiHandler!!.sendMessage(msg)
        }
    }

    /**停止录音*/
    fun stop() {
        if (mState != -1) {
            when (mState) {
                FLAG_WAV -> {
                    AudioRecordFunc.stopRecordAndFile()
                }
                FLAG_AMR -> {
                    MediaRecordFunc.stopRecordAndFile()
                }
            }
            uiThread?.stopThread()
            uiHandler?.removeCallbacks(uiThread)
            val msg = Message()
            val b = Bundle()
            b.putInt("cmd", CMD_STOP)
            b.putInt("msg", mState)
            msg.data = b

            // 向Handler发送消息,更新UI
            uiHandler?.sendMessageDelayed(msg, 1000)

            mState = -1
        }
    }

    @SuppressLint("HandlerLeak")
    class UIHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val b = msg.data
            var time = 0
            when (b.getInt("cmd")) {
                CMD_RECORDING_TIME -> {
                    time = b.getInt("msg")
                    mRecordListener?.record(RecordStatus(time, -1, audioPath, false))
                    LogUtil.i("正在录音中，已录制：$time s")
                }
                CMD_RECORD_FAIL -> {
                    val vErrorCode = b.getInt("msg")
                    val vMsg = MediaRecordFunc.ErrorCode.getErrorInfo(vErrorCode)
                    mRecordListener?.record(RecordStatus(time, -1, null, false))
                    LogUtil.i("录音失败 $vMsg")
                }
                CMD_STOP -> {
                    when (b.getInt("msg")) {
                        FLAG_WAV -> {
                            mSize = AudioRecordFunc.recordFileSize
                        }
                        FLAG_AMR -> {
                            mSize = MediaRecordFunc.recordFileSize
                        }
                    }
                    mRecordListener?.record(RecordStatus(time, mSize, audioPath, true))
                    LogUtil.i("录音已停止.录音文件:$audioPath\n文件大小：$mSize")
                }
                else -> {
                }
            }
        }
    }

    internal class UIThread : Runnable {
        var mTimeMill = -1
        var vRun = true

        fun stopThread() {
            vRun = false
        }

        override fun run() {
            while (vRun) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                mTimeMill++
                val msg = Message()
                val b = Bundle()
                b.putInt("cmd", CMD_RECORDING_TIME)
                b.putInt("msg", mTimeMill)
                msg.data = b
                // 向Handler发送消息,更新UI
                uiHandler?.sendMessage(msg)
            }
        }
    }

}
