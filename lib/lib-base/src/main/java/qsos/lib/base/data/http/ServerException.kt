package qsos.lib.base.data.http

/**
 * @author : 华清松
 * @doc description : 抛出服务器请求异常
 */
class ServerException(var code: Int, var msg: String?) : RuntimeException()