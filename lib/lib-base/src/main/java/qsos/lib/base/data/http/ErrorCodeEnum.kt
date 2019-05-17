package qsos.lib.base.data.http

/**
 * @author 华清松
 * @date  2017/3/24 0024 14:27
 * @doc 描述：错误类型
 */
enum class ErrorCodeEnum constructor(private val title: String) {
    /**访问服务器失败 */
    FAIL_LINK("访问服务器失败，请检查网络连接"),
    /**访问超时 */
    SOCKET_TIMEOUT("访问超时，服务器无响应"),
    /**运行异常 */
    APP_EXCEPTION("运行异常"),
    /**数据错误 */
    DATA_ERROR("数据错误"),
    /**解析错误 */
    OPEN_ERROR("解析错误"),
    /**未登录 */
    NO_LOGIN("请先登录");

    fun title(): String {
        return title
    }
}
