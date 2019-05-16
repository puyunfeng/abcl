package qsos.lib.base.callback

/**
 * @author : 华清松
 * @description : 类说明：泛型回调
 */
interface OnTListener<T> {

    /**获取回调对象*/
    fun getItem(item: T)
}
