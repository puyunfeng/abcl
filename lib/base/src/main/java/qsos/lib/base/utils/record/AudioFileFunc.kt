package qsos.lib.base.utils.record

import android.media.MediaRecorder
import android.os.Environment
import qsos.lib.base.utils.file.FileUtils

import java.io.File

/**
 * @author : 华清松
 * @description : 录音实现类
 */
object AudioFileFunc {
    /**音频输入-麦克风*/
    const val AUDIO_INPUT = MediaRecorder.AudioSource.MIC
    /**采用频率，44100是标准*/
    const val AUDIO_SAMPLE_RATE = 44100

    /**
     * 判断是否有外部存储设备sdcard
     */
    val isSdcardExit = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * 获取麦克风输入的原始音频流文件路径，不是最终的录音文件，将被转换为amr
     * @return
     */
    val rawFilePath: String = "${FileUtils.CHAT_PATH}/RawAudio.raw"

    /**
     * 获取编码后的WAV格式音频文件路径
     * @return
     */
    val wavFilePath: String = "${FileUtils.CHAT_PATH}/${System.currentTimeMillis()}.wav"

    /**
     * 获取编码后的AMR格式音频文件路径
     * @return
     */
    val amrFilePath: String = "${FileUtils.CHAT_PATH}/${System.currentTimeMillis()}.amr"

    /**
     * 获取文件大小
     * @param path,文件的绝对路径
     * @return
     */
    fun getFileSize(path: String): Long {
        val mFile = File(path)
        return if (!mFile.exists()) -1 else mFile.length()
    }
}