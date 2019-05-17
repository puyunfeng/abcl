package qsos.lib.netservice.file

import androidx.lifecycle.MutableLiveData
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 基础数据服务
 */
open class BaseRepository {

    // 接口回执码，用于更新统一页面
    val httpNetCode = MutableLiveData<HttpCode>()

}