package qsos.lib.netservice.file

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import qsos.lib.base.data.http.UDFileEntity
import qsos.lib.base.utils.file.FileUtils
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.ObservableService
import java.math.BigDecimal

/**
 * @author : 华清松
 * @description : 文件服务
 */
@SuppressLint("CheckResult")
class FileRepository : IFileModel, BaseRepository() {

    override fun downloadFile(fileEntity: UDFileEntity) {
        if (TextUtils.isEmpty(fileEntity.url)) {
            fileEntity.progress = -1
            dataDownloadFile.postValue(fileEntity)
        } else {
            val saveName = FileUtils.getFileNameByUrl(fileEntity.url)
            val savePath = "${FileUtils.CHAT_PATH}/$saveName"

            ApiEngine.createDownloadService(
                    ApiDownloadFile::class.java,
                    object : ProgressListener {
                        override fun progress(progress: Long, total: Long, done: Boolean) {
                            val mProgress = if (done) {
                                100
                            } else {
                                BigDecimal(progress * 100 / total)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                                        .toInt()
                            }
                            fileEntity.path = savePath
                            fileEntity.name = saveName
                            fileEntity.progress = mProgress
                            dataDownloadFile.postValue(fileEntity)
                        }
                    }).downloadFile(fileEntity.url!!)
                    .subscribeOn(Schedulers.io())
                    .map {
                        FileUtils.writeBodyToFile(savePath, it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                fileEntity.progress = 100
                                dataDownloadFile.postValue(fileEntity)
                            },
                            {
                                fileEntity.progress = -1
                                dataDownloadFile.postValue(fileEntity)
                            }
                    )
        }
    }

    override fun uploadFile(fileEntity: UDFileEntity) {
        if (TextUtils.isEmpty(fileEntity.path)) {
            fileEntity.progress = -1
            dataUploadFile.postValue(fileEntity)
        } else {
            val uploadFile = FileUtils.getFile(fileEntity.path!!)
            if (uploadFile == null) {
                fileEntity.progress = -1
                dataUploadFile.postValue(fileEntity)
            } else {
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile)
                val part = MultipartBody.Part.createFormData("file", uploadFile.name, requestBody)
                ObservableService.setFlowableBaseResult(
                        ApiEngine.createUploadService(ApiUploadFile::class.java, object : ProgressListener {
                            override fun progress(progress: Long, total: Long, done: Boolean) {
                                val mProgress = if (done) {
                                    100
                                } else {
                                    BigDecimal(progress * 100 / total)
                                            .setScale(2, BigDecimal.ROUND_HALF_UP)
                                            .toInt()
                                }
                                fileEntity.progress = mProgress
                                dataUploadFile.postValue(fileEntity)
                            }
                        }).uploadFile(part)
                ).subscribe(
                        {
                            fileEntity.url = it
                            fileEntity.progress = 100
                            dataUploadFile.postValue(fileEntity)
                        },
                        {
                            fileEntity.url = null
                            fileEntity.progress = -1
                            dataUploadFile.postValue(fileEntity)
                        }
                )
            }
        }
    }

    val dataUploadFile = MutableLiveData<UDFileEntity>()
    val dataDownloadFile = MutableLiveData<UDFileEntity>()

}