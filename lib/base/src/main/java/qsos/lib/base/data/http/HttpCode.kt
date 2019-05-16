package qsos.lib.base.data.http

/**
 * @author : 华清松
 * @description : 网络请求码类型
 */
enum class HttpCode(code: Int) {
    /*规定的*/

    /**200*/
    SUCCESS(200),
    /**401*/
    UNAUTHORIZED(401),
    /**403*/
    FORBIDDEN(403),
    /**404*/
    NOT_FOUND(404),
    /**408*/
    REQUEST_TIMEOUT(408),
    /**500*/
    INTERNAL_SERVER_ERROR(500),
    /**502*/
    BAD_GATEWAY(502),
    /**503*/
    SERVICE_UNAVAILABLE(503),
    /**504*/
    GATEWAY_TIMEOUT(504),

    /*自定义的*/

    /**请求中*/
    LOADING(999),
    /**未知错误*/
    UNKNOWN(1000),
    /**解析错误*/
    PARSE_ERROR(1001),
    /**网络错误*/
    NETWORK_ERROR(1002),
    /**协议出错*/
    HTTP_ERROR(1003),
    /**访问超时*/
    BACK_ERROR(1004),
    /**返回数据为 null，暂无数据*/
    RESULT_NULL(1005);

    val value = code
}