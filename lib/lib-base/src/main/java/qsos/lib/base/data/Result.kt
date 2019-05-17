package qsos.lib.base.data

/**
 * @author : 华清松
 * @desc : 所有实体需继承此接口
 */
interface Result<T> {
    /**请求是否报错*/
    fun isError(): Boolean

    /**请求的结果*/
    fun getResult(): T?

    /**请求码*/
    fun getResultCode(): Int

    /**回执信息*/
    fun getResultMsg(): String?
}