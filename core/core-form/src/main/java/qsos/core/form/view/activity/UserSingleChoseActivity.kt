package qsos.core.form.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.form_users.*
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.FormRepository
import qsos.core.form.view.adapter.SingleUsersAdapter
import qsos.core.form.view.other.FormItemDecoration
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.FormUserEntity
import qsos.lib.base.routepath.FormPath
import qsos.lib.base.utils.activity.ActivityUtils

/**
 * @author : 华清松
 * @description : 表单用户选择
 */
@Route(group = FormPath.FORM, path = FormPath.ITEM_USER)
class UserSingleChoseActivity : AbsFormActivity() {
    /**表单数据实现类*/
    private lateinit var formModelIml: FormModelIml

    @Autowired(name = "item_id")
    @JvmField
    var itemId: Long? = 0

    @Autowired(name = "connect_id")
    @JvmField
    var connectId: String? = ""

    private var item: FormItem? = null
    private var mList = ArrayList<FormUserEntity>()

    private var mAdapter: BaseAdapter<FormUserEntity>? = null
    private var manager: LinearLayoutManager? = null

    override val layoutId: Int = R.layout.form_users
    override val reload: Boolean = false

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        ActivityUtils.instance.addActivity(this)

        formModelIml = FormModelIml(FormRepository(mContext!!))
    }

    override fun initView() {
        super.initView()

        setSupportActionBar(tb_form_user)
        tb_form_user.setNavigationOnClickListener {
            finishThis()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""
        tv_form_user.text = "人员选择"

        mAdapter = SingleUsersAdapter(mList)
        manager = LinearLayoutManager(mContext)
        form_user_rv.layoutManager = manager as RecyclerView.LayoutManager?
        form_user_rv.addItemDecoration(FormItemDecoration())
        form_user_rv.adapter = mAdapter

        form_user_search.setOnClickListener {
            getData()
        }

        formModelIml.dbFormItem.observe(this, Observer {
            item = it
            formModelIml.getUsers(connectId, item!!, form_users_et.text.toString())
        })
        formModelIml.userList.observe(this, Observer {
            if (it != null) {
                mList.clear()
                mList.addAll(it)
                mAdapter!!.notifyDataSetChanged()
            } else {
                showToast("查询可选列表失败")
            }
        })

        getData()

    }

    override fun getData() {
        if (itemId != null) {
            formModelIml.getFormItemByDB(itemId!!)
        }
    }

}
