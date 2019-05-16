package qsos.lib.base.data

import qsos.lib.base.data.http.ErrorCodeEnum
import qsos.lib.base.utils.LogUtil
import retrofit2.HttpException
import java.net.SocketTimeoutException

/**
 * @author 华清松
 * @doc 类说明：全局通用请求结果实体
 */
class BaseResult<T> : Result<T> {
    /**请求成功与否*/
    var state: Boolean = true
    /**返回信息展示，如请求成功*/
    var msg: String? = null
    /**响应时间*/
    var time: Long = 0
    /**总页数，分页才使用*/
    var total: Int = 0
    /**是否有更多*/
    var more: Boolean = false
    /**请求返回码，成功200*/
    var code: Int = 0
    /**错误类型，仅前端使用*/
    var errorCodeEnum: ErrorCodeEnum? = null

    /**请求结果*/
    var results: T? = null

    constructor()

    constructor(state: Boolean, message: String, results: T) {
        this.state = state
        this.msg = message
        this.errorCodeEnum = null
        this.results = results
    }

    constructor(e: Throwable) {
        this.msg = e.message
        this.state = false
        LogUtil.e("" + e.message)
        try {
            val httpException = e as HttpException
            when (httpException.code()) {
                504 -> this.errorCodeEnum = ErrorCodeEnum.FAIL_LINK
                else -> this.errorCodeEnum = ErrorCodeEnum.APP_EXCEPTION
            }
        } catch (ex1: Exception) {
            // 链接超时
            try {
                e as SocketTimeoutException
                this.errorCodeEnum = ErrorCodeEnum.SOCKET_TIMEOUT
            } catch (ex2: Exception) {
                this.errorCodeEnum = ErrorCodeEnum.APP_EXCEPTION
            }
        }

    }

    override fun isError(): Boolean {
        return !state
    }

    override fun getResult(): T? {
        return results
    }

    override fun getResultCode(): Int {
        return code
    }

    override fun getResultMsg(): String? {
        return msg
    }
}
