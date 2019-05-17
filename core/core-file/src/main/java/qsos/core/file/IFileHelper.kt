package qsos.core.file

import com.qingmei2.rximagepicker.entity.Result
import io.reactivex.functions.Consumer
import qsos.lib.base.callback.OnTListener
import java.io.File

/**
 * @author : 华清松
 * @description : 文件操作接口
 */
interface IFileHelper {

    interface ITakeFile {
        /**打开摄像头*/
        fun takeCamera(next: Consumer<Result>)

        /**打开相册
         * @since 必须开启 Manifest.permission.CAMERA 权限
         * */
        fun takeGallery(next: Consumer<Result>)

        /**
         * 打开文件选择
         * @param mimeTypes 可选文件类型
         * @param TAKE_FILE_CODE 选择回调码
         */
        fun takeFile(mimeTypes: ArrayList<String>, TAKE_FILE_CODE: Int)

        /**上传图片前进行压缩
         * @param file 需要压缩的文件
         * @param listener OnTListener<File?> 压缩失败返回 <null>
         */
        fun uploadImageBeforeZip(file: File?, listener: OnTListener<File?>?)
    }

    /**打开文件的接口*/
    interface IOpen {
        /**
         * 本地应用打开文件
         * @param path 文件路径
         */
        fun openFile(path: String)
    }

    interface OnSendFileListener<T> {
        /**发送文件*/
        fun send(file: File, type: T)
    }

    interface OnSendLocationListener {
        /**发送定位*/
        fun send(x: Double, y: Double)
    }
}