package qsos.base.core

import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import qsos.core.file.FileApplication

/**
 * @author : 华清松
 * @description : 通用业务模块 Application ，你可以重写此类已替换刷新样式
 */
abstract class ModelApplication : FileApplication() {
    init {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, R.color.black)
            ClassicsHeader(context) as RefreshHeader
        }
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, R.color.black)
            ClassicsFooter(context)
        }
    }
}