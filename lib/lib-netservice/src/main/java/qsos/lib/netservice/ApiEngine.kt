package qsos.lib.netservice

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import qsos.lib.base.BuildConfig
import qsos.lib.netservice.file.ProgressListener
import qsos.lib.netservice.interceptor.DownloadFileInterceptor
import qsos.lib.netservice.interceptor.NetWorkInterceptor
import qsos.lib.netservice.interceptor.UploadFileInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author 华清松
 * @doc 类说明：使用Retrofit+OkHttp搭建网路请求框架
 */
object ApiEngine {

    private var mHost: String = BuildConfig.TEST_URL
    private var mBuild: Retrofit.Builder
    private var mClient: OkHttpClient.Builder

    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create(GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create())

    init {
        mBuild = Retrofit.Builder()
                .baseUrl(mHost)
                .addConverterFactory(gsonFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        mClient = OkHttpClient.Builder()
                // 网络拦截器
                .addNetworkInterceptor(NetWorkInterceptor())
                // 日志拦截器
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    }

    /**自行配置请调用此方法*/
    fun init(retrofitBuilder: Retrofit.Builder?, clientBuilder: OkHttpClient.Builder?): ApiEngine {
        if (retrofitBuilder != null) {
            mBuild = retrofitBuilder
        }
        if (clientBuilder != null) {
            mClient = clientBuilder
        }
        return this
    }

    var isUnitTest = false

    /**
     * 创建普通服务
     */
    fun <T> createService(service: Class<T>): T {
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }

    /**
     * 创建普通服务，变更host
     */
    fun <T> createService(service: Class<T>, host: String): T {
        if (mBuild.build().baseUrl().host() != host) {
            mBuild = Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }

    /**
     * 创建文件上传服务
     * @param listener 进度监听
     */
    fun <T> createUploadService(service: Class<T>, listener: ProgressListener?): T {
        mClient.addInterceptor(UploadFileInterceptor(listener))
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }

    /**
     * 创建文件下载服务
     * @param listener 进度监听
     */
    fun <T> createDownloadService(service: Class<T>, listener: ProgressListener?): T {
        mClient.addInterceptor(DownloadFileInterceptor(listener))
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }
}
