package qsos.core.file

import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsDownloader
import com.tencent.smtt.sdk.TbsListener
import qsos.lib.base.base.BaseApplication
import timber.log.Timber

/**
 * @author : 华清松
 * @description : 文件服务
 */
abstract class FileApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        val preInitCallback = object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
            }

            override fun onViewInitFinished(b: Boolean) {
            }
        }

        // tbs 内核下载跟踪
        val tbsListener = object : TbsListener {
            override fun onDownloadFinish(i: Int) {
                //tbs内核下载完成回调
                Timber.tag("X5内核").i("下载完成")
            }

            override fun onInstallFinish(i: Int) {
                //内核安装完成回调
                Timber.tag("X5内核").i("安装完成")
            }

            override fun onDownloadProgress(i: Int) {
                //下载进度监听
            }
        }
        QbSdk.initX5Environment(this, preInitCallback)
        // tbs 内核下载跟踪
        QbSdk.setTbsListener(tbsListener)
        // 判断是否要自行下载内核
        QbSdk.setDownloadWithoutWifi(true)
        val needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS)
        if (needDownload) {
            TbsDownloader.startDownload(this)
        }

    }
}