package qsos.core.lib.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 通用业务处理
 */
class BaseModelIml(
        private val baseModelRepo: BaseModuleRepository
) : ViewModel() {

    fun setHttpCode(httpCode: HttpCode) {
        baseModelRepo.setHttpCode(httpCode)
    }

    // 接口回执码，用于更新统一页面
    val httpNetCode: LiveData<HttpCode> = Transformations.map(baseModelRepo.httpNetCode) { it }

}