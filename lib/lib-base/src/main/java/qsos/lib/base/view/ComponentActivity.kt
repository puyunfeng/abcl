package qsos.lib.base.view

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.component_list_main.*
import qsos.lib.base.R
import qsos.lib.base.base.BaseNormalActivity
import qsos.lib.base.routepath.BasePath

/**
 * @author : 华清松
 * @description : 功能列表页面
 */
@Route(group = BasePath.GROUP, path = BasePath.MAIN)
class ComponentActivity : BaseNormalActivity() {

    private lateinit var mAdapter: ComponentAdapter
    override val layoutId = R.layout.component_list_main

    override val reload = true

    override fun initData(savedInstanceState: Bundle?) {}

    override fun initView() {
        mAdapter = ComponentAdapter(ComponentItemEntity.getList())
        component_rv.layoutManager = LinearLayoutManager(mContext)
        component_rv.adapter = mAdapter
    }

    override fun getData() {}

}