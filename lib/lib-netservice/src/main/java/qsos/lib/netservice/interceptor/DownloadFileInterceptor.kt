package qsos.lib.netservice.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import qsos.lib.netservice.file.ProgressListener
import qsos.lib.netservice.file.DownloadBody
import java.io.IOException

/**
 * @author : 华清松
 * @description : 文件下载拦截器
 */
class DownloadFileInterceptor(private val listener: ProgressListener?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val body = response.body()
        return if (body != null) {
            response.newBuilder()
                    .body(DownloadBody(body, listener))
                    .build()
        } else {
            response
        }
    }
}