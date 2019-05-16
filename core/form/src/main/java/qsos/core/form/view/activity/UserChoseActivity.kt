package qsos.core.form.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.form_users.*
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.FormUserEntity
import qsos.lib.base.data.form.Value
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.data.http.ServerException
import qsos.lib.base.routepath.FormPath
import qsos.lib.base.utils.activity.ActivityUtils
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.FormRepository
import qsos.core.form.view.adapter.FormUsersAdapter
import qsos.core.form.view.other.FormItemDecoration

/**
 * @author : 华清松
 * @description : 表单用户选择
 */
@Route(group = FormPath.FORM, path = FormPath.ITEM_USERS)
class UserChoseActivity : AbsFormActivity(), Toolbar.OnMenuItemClickListener {
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

    private var chose = 0
    private var limitMin: Int? = 0
    private var limitMax: Int? = 0

    private var mAdapter: FormUsersAdapter? = null
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
        tb_form_user.setNavigationOnClickListener { _ ->
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""
        tb_form_user.setOnMenuItemClickListener(this)
        tv_form_user.text = "人员选择"

        mAdapter = FormUsersAdapter(mList)
        mAdapter?.setOnChoseListener(object : OnTListener<Int> {
            override fun getItem(item: Int) {
                chose = item
                changeChoseUser()
            }
        })
        manager = LinearLayoutManager(mContext)
        form_user_rv.layoutManager = manager as RecyclerView.LayoutManager?
        form_user_rv.addItemDecoration(FormItemDecoration())
        form_user_rv.adapter = mAdapter

        form_user_search.setOnClickListener {
            hideKeyboard()
            getData()
        }

        tv_form_user_chose_all.setOnClickListener {
            mAdapter?.changeAllChose(true)
        }
        tv_form_user_chose_cancel.setOnClickListener {
            mAdapter?.changeAllChose(false)
        }

        form_users_et.setOnTouchListener(View.OnTouchListener { _, _ ->
            form_users_et.isFocusable = true
            form_users_et.isFocusableInTouchMode = true
            return@OnTouchListener false
        })

        form_users_et.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideKeyboard()
                form_users_et.isFocusable = false
                form_users_et.isFocusableInTouchMode = false
                formModelIml.getUsers(connectId, item!!, form_users_et.text.toString())
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })

        formModelIml.dbFormItem.observe(this, Observer {
            item = it
            formModelIml.getUsers(connectId, item!!, form_users_et.text.toString())
        })

        formModelIml.userList.observe(this, Observer {
            if (it == null) {
                showToast("查询可选列表数据失败")
            } else {
                mList.clear()
                mList.addAll(it)

                chose = getValues().size
                limitMin = item?.form_item_value?.limit_min
                limitMax = item?.form_item_value?.limit_max

                changeChoseUser()

                mAdapter?.setLimit(chose, limitMin, limitMax)
                mAdapter?.notifyDataSetChanged()
            }
        })

        getData()

    }

    override fun getData() {
        if (itemId != null) {
            formModelIml.getFormItemByDB(itemId!!)
        }
    }

    private fun getValues(): List<Value> {
        return item?.form_item_value?.values!!
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("确认")?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        finish()
        return true
    }

    @SuppressLint("SetTextI18n")
    fun changeChoseUser() {
        if (limitMax != 1) {
            ll_form_user_chose.visibility = View.VISIBLE
            tv_form_user_chose_all.visibility = if (limitMax == -1) View.VISIBLE else View.GONE
            val limitMaxUser = if (limitMax == null || limitMax == -1) "可选人数不限" else "可选 $limitMax 人"
            tv_form_user_chose_num.text = "已选 $chose 人，$limitMaxUser"
        } else {
            ll_form_user_chose.visibility = View.GONE
        }
    }
}
