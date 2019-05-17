package qsos.lib.netservice.file

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * @author : 华清松
 * @description : 下载请求 Body
 */
class DownloadBody(
        /**实际请求体*/
        private val responseBody: ResponseBody,
        /**上传进度监听*/
        private val listener: ProgressListener?
) : ResponseBody() {

    // 包装完成的BufferedSource
    private var bufferedSource: BufferedSource? = null

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private fun source(source: Source): Source {

        return object : ForwardingSource(source) {
            //当前读取字节数
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // 增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0L
                // 回调，如果 contentLength() 不知道长度，会返回-1
                listener?.progress(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                return bytesRead
            }
        }
    }

}