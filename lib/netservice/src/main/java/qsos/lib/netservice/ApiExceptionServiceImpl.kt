package qsos.lib.netservice

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.data.http.ServerException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * @author 华清松
 * @doc 类说明：HTTP 请求异常统一处理类
 */
object ApiExceptionServiceImpl {

    fun handleException(e: Throwable): ApiException {
        val ex: ApiException
        if (e is HttpException) {
            // HTTP错误
            ex = when (e.code()) {
                HttpCode.UNAUTHORIZED.value -> ApiException(e, HttpCode.UNAUTHORIZED)
                HttpCode.NOT_FOUND.value -> ApiException(e, HttpCode.NOT_FOUND)
                else -> ApiException(e, HttpCode.HTTP_ERROR)
            }
        } else if (e is ServerException) {
            // 服务器定义的错误
            ex = when (e.code) {
                HttpCode.UNAUTHORIZED.value -> ApiException(e, HttpCode.UNAUTHORIZED)
                HttpCode.NOT_FOUND.value -> ApiException(e, HttpCode.NOT_FOUND)
                HttpCode.RESULT_NULL.value -> ApiException(e, HttpCode.RESULT_NULL)
                else -> ApiException(e, HttpCode.HTTP_ERROR)
            }
            Timber.tag("服务器异常").e(e.msg)
        } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException) {
            // 均视为解析错误
            ex = ApiException(e, HttpCode.PARSE_ERROR)
            Timber.tag("数据解析异常").e("$e")
        } else if (e is ConnectException) {
            // 均视为网络错误
            ex = ApiException(e, HttpCode.NETWORK_ERROR)
            Timber.tag("网络连接失败").e("$e")
        } else if (e is SocketTimeoutException) {
            // 访问超时
            ex = ApiException(e, HttpCode.BACK_ERROR)
            Timber.tag("服务器超时").e("$e")
        } else if (e is NullPointerException) {
            ex = ApiException(e, HttpCode.NETWORK_ERROR)
            Timber.tag("空指针异常，访问路径对么？").e("$e")
        } else {
            ex = ApiException(e, HttpCode.UNKNOWN)
            Timber.tag("未知异常").e("$e")
        }
        return ex
    }
}
