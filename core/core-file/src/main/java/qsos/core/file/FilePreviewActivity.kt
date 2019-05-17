package qsos.core.file

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.file_activity_file.*
import qsos.lib.base.base.BaseNormalActivity
import qsos.lib.base.data.http.UDFileEntity
import qsos.lib.base.routepath.FilePath
import qsos.lib.base.utils.activity.ActivityUtils
import qsos.lib.base.utils.file.FileUtils
import qsos.lib.netservice.file.FileRepository
import java.io.File

/**
 * @author : 华清松
 * @description : 文件预览界面，本地下载后打开
 */
@Route(group = FilePath.GROUP, path = FilePath.FILE_PREVIEW)
class FilePreviewActivity(override val layoutId: Int? = R.layout.file_activity_file)
    : BaseNormalActivity() {

    @Autowired(name = FilePath.FILE_URL)
    @JvmField
    var fileUrl: String? = null
    @Autowired(name = FilePath.FILE_NAME)
    @JvmField
    var fileName: String? = null
    @Autowired(name = FilePath.FILE_PATH)
    @JvmField
    var filePath: String? = null

    private var mFile: File? = null

    private val fileRepo = FileRepository()

    override fun initData(savedInstanceState: Bundle?) {
        ActivityUtils.instance.addActivity(this)

        if (!TextUtils.isEmpty(filePath)) {
            mFile = FileUtils.getFile(filePath!!)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        setSupportActionBar(file_preview_file_tb)
        file_preview_file_tb.setNavigationOnClickListener {
            finishThis()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""

        file_preview_file_name.text = fileName

        /**判断文件在本地还是在网络，在本地-可打开，在网络-需下载完后打开*/
        when {
            mFile != null -> {
                file_preview_file_notice.text = "文件路径 $filePath"
                file_preview_file_open.text = "本地打开"
                file_preview_file_open.isClickable = true
            }
            !TextUtils.isEmpty(fileUrl) -> {
                file_preview_file_notice.text = "此文件不支持在线预览，请下载后打开"
                file_preview_file_open.text = "下载文件"
            }
            else -> {
                file_preview_file_notice.text = "文件不存在，无法预览"
                file_preview_file_open.isClickable = false
            }
        }

        file_preview_file_open.setOnClickListener { view ->
            view.isClickable = false
            if (mFile == null) {
                getData()
            } else {
                mRxPermissions?.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                        ?.subscribe {
                            view.isClickable = true
                            if (it) {
                                try {
                                    FileUtils.openFileOnPhone(this@FilePreviewActivity, mFile!!)
                                } catch (e: Exception) {
                                    showToast("文件打开失败 $e")
                                }
                            } else {
                                showToast("授权失败，无法预览文件")
                            }
                        }
            }
        }

        /**下载回调*/
        fileRepo.dataDownloadFile.observe(this, Observer {
            when {
                it.progress in 0..99 -> {
                    file_preview_file_notice.text = "下载中 ${it.progress} %"
                    file_preview_file_open.text = "下载中"
                    file_preview_file_open.isClickable = false
                }
                it.progress == 100 -> {
                    mFile = FileUtils.getFile(it.path!!)
                    if (mFile != null) {
                        file_preview_file_notice.text = "下载成功 路径 ${it.path}"
                        file_preview_file_open.text = "本地打开"
                        file_preview_file_open.isClickable = true
                    } else {
                        file_preview_file_notice.text = "下载失败，请重试"
                        file_preview_file_open.text = "重新下载"
                        file_preview_file_open.isClickable = true
                    }
                }
                else -> {
                    file_preview_file_notice.text = "下载失败，请重试"
                    file_preview_file_open.text = "重新下载"
                    file_preview_file_open.isClickable = true
                }
            }
        })
    }

    override fun getData() {
        if (mFile == null) {
            fileRepo.downloadFile(UDFileEntity(fileUrl, filePath, fileName, 0))
        }
    }

}
