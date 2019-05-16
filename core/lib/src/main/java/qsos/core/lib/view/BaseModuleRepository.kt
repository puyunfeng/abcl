package qsos.core.lib.view

import qsos.lib.base.data.http.HttpCode
import qsos.lib.netservice.file.BaseRepository

/**
 * @author : 华清松
 * @description : 网络请求数据获取
 */
class BaseModuleRepository : BaseRepository() {
    fun setHttpCode(httpCode: HttpCode) {
        httpNetCode.postValue(httpCode)
    }
}