package qsos.lib.netservice.file

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import java.io.IOException

/**
 * @author : 华清松
 * @description : 上传请求 Body
 */
class UploadBody(
        /**实际请求体*/
        private val requestBody: RequestBody,
        /**上传进度监听*/
        private val listener: ProgressListener?
) : RequestBody() {

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    /**
     * 重写进行写入
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        // 将请求体上传的文件数据写入 sink 流对象，向服务器写数据
        requestBody.writeTo(sink)
        // NOTICE 必须调用flush
        sink.flush()
    }

    /**
     * 写入，回调进度接口
     * @param sink Sink
     * @return Sink
     */
    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            // 当前写入字节数
            var bytesWritten = 0L
            // 总字节长度，避免多次调用 contentLength() 方法
            var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    // 获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                // 增加当前写入的字节数
                bytesWritten += byteCount
                // 进度回调
                listener?.progress(bytesWritten, contentLength, bytesWritten == contentLength)
            }
        }
    }
}