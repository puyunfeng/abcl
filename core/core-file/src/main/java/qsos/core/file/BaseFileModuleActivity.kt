package qsos.core.file

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import com.qingmei2.rximagepicker.entity.Result
import io.reactivex.functions.Consumer
import qsos.core.lib.view.BaseModuleActivity
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.data.app.FileEntity
import qsos.lib.base.routepath.MapPath
import qsos.lib.base.utils.file.FileUtils
import java.io.File

/**
 * @author : 华清松
 * @description : 业务模块公共 Activity
 */
@SuppressLint("CheckResult")
abstract class BaseFileModuleActivity : BaseModuleActivity(), IFileHelper.ITakeFile {

    private var mSendFileListener: IFileHelper.OnSendFileListener<FileEntity.FileTypeEnum>? = null
    private var mSendLocationListener: IFileHelper.OnSendLocationListener? = null

    /**设置文件上传监听*/
    fun setOnSendFileListener(fileListener: IFileHelper.OnSendFileListener<FileEntity.FileTypeEnum>) {
        this.mSendFileListener = fileListener
    }

    /**设置位置选择监听*/
    fun setOnSendLocationListener(locationListener: IFileHelper.OnSendLocationListener) {
        this.mSendLocationListener = locationListener
    }

    override fun uploadImageBeforeZip(file: File?, listener: OnTListener<File?>?) {
        if (file == null) {
            showToast("图片获取失败，无法上传，请重新选择")
            return
        }
        TakeFileHelper(this).uploadImageBeforeZip(file, listener ?: object : OnTListener<File?> {
            override fun getItem(item: File?) {
                if (item == null) {
                    showToast("图片处理失败，无法上传，请重新选择")
                } else {
                    mSendFileListener?.send(item, FileEntity.FileTypeEnum.IMAGE)
                }
            }
        })
    }

    override fun takeCamera(next: Consumer<Result>) {
        mRxPermissions?.request(Manifest.permission.CAMERA)!!.subscribe {
            if (it) {
                TakeFileHelper(this).takeCamera(next)
            } else {
                showToast("没有权限")
            }
        }
    }

    override fun takeGallery(next: Consumer<Result>) {
        mRxPermissions?.request(Manifest.permission.CAMERA)!!.subscribe {
            if (it) {
                TakeFileHelper(this).takeGallery(next)
            } else {
                showToast("没有权限")
            }
        }
    }

    override fun takeFile(mimeTypes: ArrayList<String>, TAKE_FILE_CODE: Int) {
        TakeFileHelper(this).takeFile(mimeTypes, TAKE_FILE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FileUtils.TAKE_VIDEO_CODE -> {
                if (data != null) {
                    val videoPath = data.getStringExtra("video_path")
                    val file = File(videoPath)
                    // 上传视频
                    mSendFileListener?.send(file, FileEntity.FileTypeEnum.VIDEO)
                }
            }
            FileUtils.TAKE_LOCATION_CODE -> {
                if (data != null) {
                    val locationX = data.getDoubleExtra(MapPath.MAIN_KEY_LOCATION_X, 0.0)
                    val locationY = data.getDoubleExtra(MapPath.MAIN_KEY_LOCATION_Y, 0.0)
                    // 上传位置
                    mSendLocationListener?.send(locationX, locationY)
                }
            }
            FileUtils.TAKE_FILE_CODE -> {
                RxTakeFileHelper.getFileFromData(this, data)
                        .subscribe(object : RxTakeFileHelper.TakeFileObserver() {

                            override fun onError(e: Throwable) {
                                showToast("文件选择失败，请重试")
                            }

                            override fun onNext(fileList: List<File>) {
                                for (file in fileList) {
                                    if (FileEntity.getFileType(file.extension) == FileEntity.FileTypeEnum.WORD) {
                                        // 上传文档
                                        mSendFileListener?.send(file, FileEntity.FileTypeEnum.WORD)
                                    } else {
                                        showToast("不支持此类型的文档上传")
                                    }
                                }
                            }
                        })
            }
        }
    }

}
