package qsos.lib.netservice

import io.reactivex.functions.Function
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.data.Result
import qsos.lib.base.data.http.ServerException

/**
 * @author : 华清松
 * @description : 拦截固定格式的公共数据类型 Response<T>,判断里面状态码 <T>
 */
class ApiResponseFunc<T> : Function<Result<T>, T> {

    @Throws(ApiException::class)
    override fun apply(result: Result<T>): T {
        if (!result.isError()) {
            throw ServerException(result.getResultCode(), result.getResultMsg())
        }
        if (result.getResult() == null && result.getResultCode() == 200) {
            throw ServerException(HttpCode.PARSE_ERROR.value, "解析错误")
        }
        when (result.getResultCode()) {
            401 -> throw ServerException(401, result.getResultMsg())
            404 -> throw ServerException(404, result.getResultMsg())
            500 -> throw ServerException(500, result.getResultMsg())
        }
        return result.getResult()!!
    }
}