package qsos.core.file

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import qsos.lib.base.utils.file.FileUtils
import java.io.File

/**
 * @author : 华清松
 * @description : Rx 文件选择，源代码地址：https://github.com/pavlospt/RxFile
 */
object RxTakeFileHelper {

    /**
     * 从Intent中获取文件信息
     * @param intent 回调 INTENT
     * @return 成功返回 Observable<List<File>> 失败返回 Observable.error<List<File>>(e)
     */
    fun getFileFromData(context: Context, intent: Intent?): Observable<List<File>> {
        return if (intent == null) {
            Observable.just(arrayListOf())
        } else {
            when {
                intent.data != null -> getFileFromUri(context, intent.data!!)
                intent.clipData != null -> getFilesFromClipData(context, intent.clipData!!)
                else -> Observable.error<List<File>>(NullPointerException())
            }
        }
    }

    /**单个文件获取*/
    private fun getFileFromUri(context: Context, data: Uri): Observable<List<File>> {
        val files = arrayListOf<File>()
        return Observable.defer {
            try {
                val file = FileUtils.fileFromUri(context, data)
                if (file != null) {
                    files.add(file)
                    Observable.just(if (files.isEmpty()) null else files)
                } else {
                    Observable.error<List<File>>(NullPointerException())
                }
            } catch (e: Exception) {
                Observable.error<List<File>>(e)
            }
        }
    }

    /**多个文件获取*/
    private fun getFilesFromClipData(context: Context, clipData: ClipData): Observable<List<File>> {
        val files = arrayListOf<File>()
        return Observable.defer {
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                if (uri != null) {
                    try {
                        val file = FileUtils.fileFromUri(context, uri)
                        if (file != null) files.add(file)
                    } catch (e: Exception) {
                        Observable.error<List<File>>(e)
                    }
                }
            }
            Observable.just<List<File>>(files)
        }
    }

    /**文件选择回调统一处理，一般需要重写 onNext 和 onError */
    abstract class TakeFileObserver : Observer<List<File>> {
        override fun onComplete() {
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onNext(fileList: List<File>) {
        }

        override fun onError(e: Throwable) {
        }

    }
}
