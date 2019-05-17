package qsos.lib.base.utils.record

import android.media.AudioFormat
import android.media.AudioRecord
import java.io.*

/**
 * @author : 华清松
 * @description : 录音文件操作
 */
object AudioRecordFunc {
    // 缓冲区字节大小
    private var bufferSizeInBytes = 0

    //AudioName裸音频数据文件 ，麦克风
    private var mAudioName = ""

    //NewAudioName可播放的音频文件
    private var mNewAudioName = ""

    private var audioRecord: AudioRecord? = null
    // 设置正在录制的状态
    private var isRecord = false

    val recordFileSize: Long = AudioFileFunc.getFileSize(mNewAudioName)

    fun startRecordAndFile(): Int {
        //判断是否有外部存储设备sdcard
        if (AudioFileFunc.isSdcardExit) {
            return if (isRecord) {
                MediaRecordFunc.ErrorCode.E_STATE_RECODING
            } else {
                if (audioRecord == null) {
                    createAudioRecord()
                }
                audioRecord?.startRecording()
                // 让录制状态为true
                isRecord = true
                // 开启音频文件写入线程
                Thread(AudioRecordThread()).start()

                MediaRecordFunc.ErrorCode.SUCCESS
            }

        } else {
            return MediaRecordFunc.ErrorCode.E_NO_SD_CARD
        }
    }

    fun stopRecordAndFile() {
        isRecord = false
        try {
            audioRecord?.stop()
        } catch (e: Exception) {
        }
        audioRecord?.release()
        audioRecord = null
    }

    private fun createAudioRecord() {
        // 获取音频文件路径
        mAudioName = AudioFileFunc.rawFilePath
        mNewAudioName = AudioUtils.audioPath
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AudioFileFunc.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        // 创建AudioRecord对象
        audioRecord = AudioRecord(AudioFileFunc.AUDIO_INPUT, AudioFileFunc.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes)
    }

    internal class AudioRecordThread : Runnable {
        override fun run() {
            // 往文件中写入裸数据
            writeDateTOFile()
            // 给裸数据加上头文件
            copyWaveFile(mAudioName, mNewAudioName)
        }
    }

    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */
    private fun writeDateTOFile() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        val audioData = ByteArray(bufferSizeInBytes)
        var fos: FileOutputStream? = null
        var readSize: Int
        try {
            val file = File(mAudioName)
            if (file.exists()) {
                file.delete()
            }
            // 建立一个可存取字节的文件
            fos = FileOutputStream(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        while (isRecord) {
            readSize = audioRecord!!.read(audioData, 0, bufferSizeInBytes)
            if (AudioRecord.ERROR_INVALID_OPERATION != readSize && fos != null) {
                try {
                    fos.write(audioData)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        try {
            // 关闭写入流
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // 这里得到可播放的音频文件
    private fun copyWaveFile(inFilename: String, outFilename: String) {
        val `in`: FileInputStream
        val out: FileOutputStream
        val totalAudioLen: Long
        val totalDataLen: Long
        val longSampleRate = AudioFileFunc.AUDIO_SAMPLE_RATE.toLong()
        val channels = 2
        val byteRate = (16 * AudioFileFunc.AUDIO_SAMPLE_RATE * channels / 8).toLong()
        val data = ByteArray(bufferSizeInBytes)
        try {
            `in` = FileInputStream(inFilename)
            out = FileOutputStream(outFilename)
            totalAudioLen = `in`.channel.size()
            totalDataLen = totalAudioLen + 36
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate)
            while (`in`.read(data) != -1) {
                out.write(data)
            }
            `in`.close()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     * 自己特有的头文件。
     */
    @Throws(IOException::class)
    private fun WriteWaveFileHeader(out: FileOutputStream, totalAudioLen: Long, totalDataLen: Long, longSampleRate: Long, channels: Int, byteRate: Long) {
        val header = ByteArray(44)
        header[0] = 'R'.toByte() // RIFF/WAVE header
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte() // 'fmt ' chunk
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }

}