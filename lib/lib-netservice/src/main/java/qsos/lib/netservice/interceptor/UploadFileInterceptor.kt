package qsos.lib.netservice.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import qsos.lib.netservice.file.ProgressListener
import qsos.lib.netservice.file.UploadBody
import java.io.IOException

/**
 * @author : 华清松
 * @description : 文件上传拦截器
 */
class UploadFileInterceptor(private val listener: ProgressListener?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val body = original.body()
        val request: Request
        return if (body != null) {
            request = original.newBuilder()
                    .method(original.method(), UploadBody(body, listener))
                    .build()
            chain.proceed(request)
        } else {
            chain.proceed(original)
        }
    }
}