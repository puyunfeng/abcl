package qsos.core.file

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker.entity.Result
import io.reactivex.functions.Consumer
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.file.FileUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * @author : 华清松
 * @description : 拍照、图片、文件帮助类
 */
@SuppressLint("CheckResult")
class TakeFileHelper(context: Context) : IFileHelper.ITakeFile, IFileHelper.IOpen {
    private var mContext: Context? = context

    override fun takeCamera(next: Consumer<Result>) {
        RxImagePicker.create(IImagePicker::class.java)
                .openCamera(mContext!!)
                .subscribe(next, onError())
    }

    override fun takeGallery(next: Consumer<Result>) {
        RxImagePicker.create(IImagePicker::class.java)
                .openGallery(mContext!!)
                .subscribe(next, onError())
    }

    override fun takeFile(mimeTypes: ArrayList<String>, TAKE_FILE_CODE: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var types = ""
            for (type in mimeTypes) {
                types += "$type|"
            }
            intent.type = types.substring(0, types.length - 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }

        intent.addCategory(Intent.CATEGORY_OPENABLE)
        (mContext!! as Activity).startActivityForResult(Intent.createChooser(intent, "Pick files"), TAKE_FILE_CODE)
    }

    override fun uploadImageBeforeZip(file: File?, listener: OnTListener<File?>?) {
        Luban.with(mContext).load(File(file?.path)).setCompressListener(object : OnCompressListener {
            override fun onStart() {

            }

            override fun onSuccess(newFile: File?) {
                listener?.getItem(newFile)
            }

            override fun onError(e: Throwable?) {
                listener?.getItem(null)
            }
        }).launch()
    }

    /**拍照、相册选择失败*/
    private fun onError(): Consumer<Throwable> {
        return Consumer { e ->
            e.printStackTrace()
            ToastUtils.showToast(mContext, "选择错误 $e")
        }
    }

    override fun openFile(path: String) {
        try {
            FileUtils.openFileOnPhone(mContext as Activity, File(path))
        } catch (e: Exception) {
            ToastUtils.showToast(mContext, "打开错误 $e")
        }
    }

}