package qsos.lib.base.utils.record

import android.content.res.Resources.NotFoundException
import android.media.MediaRecorder
import java.io.File
import java.io.IOException

/**
 * @author : 华清松
 * @description : 录音控制类
 */
object MediaRecordFunc {
    private var isRecord = false

    private var mMediaRecorder: MediaRecorder? = null

    val recordFileSize: Long = AudioFileFunc.getFileSize(AudioUtils.audioPath)

    fun startRecordAndFile(): Int {
        /**判断是否有外部存储设备sdcard*/
        if (AudioFileFunc.isSdcardExit) {
            if (isRecord) {
                return ErrorCode.E_STATE_RECODING
            } else {
                if (mMediaRecorder == null) {
                    createMediaRecord()
                }
                return try {
                    mMediaRecorder!!.prepare()
                    mMediaRecorder!!.start()
                    // 让录制状态为true
                    isRecord = true
                    ErrorCode.SUCCESS
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    ErrorCode.E_UN_KNOW
                }
            }

        } else {
            return ErrorCode.E_NO_SD_CARD
        }
    }

    fun stopRecordAndFile() {
        isRecord = false
        try {
            mMediaRecorder?.stop()
        } catch (e: Exception) {
        }
        mMediaRecorder?.release()
        mMediaRecorder = null
    }

    private fun createMediaRecord() {
        /**实例化MediaRecorder对象 */
        mMediaRecorder = MediaRecorder()
        // 设置麦克风
        mMediaRecorder!!.setAudioSource(AudioFileFunc.AUDIO_INPUT)

        /**
         * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
         * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
         */
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)

        /**设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
        mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)

        /**设置输出文件的路径 */
        val file = File(AudioUtils.audioPath)
        if (file.exists()) {
            file.delete()
        }
        mMediaRecorder!!.setOutputFile(AudioUtils.audioPath)
    }

    object ErrorCode {
        const val SUCCESS = 1000
        const val E_NO_SD_CARD = 1001
        const val E_STATE_RECODING = 1002
        const val E_UN_KNOW = 1003

        @Throws(NotFoundException::class)
        fun getErrorInfo(vType: Int): String {
            return when (vType) {
                SUCCESS -> "成功"
                E_NO_SD_CARD -> "无SD卡"
                else -> "未知错误"
            }
        }

    }
}
