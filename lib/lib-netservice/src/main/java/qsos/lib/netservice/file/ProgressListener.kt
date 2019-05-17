package qsos.lib.netservice.file

/**
 * @author : 华清松
 * @description : 文件上传/下载进度监听接口
 */
interface ProgressListener {

    /**@param progress 已有长度
     * @param total 总长度
     * @param done 是否完成
     */
    fun progress(progress: Long, total: Long, done: Boolean)
}