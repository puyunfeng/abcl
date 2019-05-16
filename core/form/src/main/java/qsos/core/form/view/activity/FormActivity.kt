package qsos.core.form.view.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.form_activity_main.*
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.FormRepository
import qsos.core.form.view.adapter.FormAdapter
import qsos.core.form.view.other.FormItemDecoration
import qsos.core.form.view.widget.pop.PopupWindowUtils
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.data.form.FormEntity
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.FormType
import qsos.lib.base.routepath.FormPath
import qsos.lib.base.utils.activity.ActivityUtils
import qsos.lib.base.utils.file.FileUtils

/**
 * @author : 华清松
 * @description : 表单界面
 */
@Route(group = FormPath.FORM, path = FormPath.MAIN)
class FormActivity : AbsFormActivity() {

    /**表单数据实现类*/
    private lateinit var formModelIml: FormModelIml

    @Autowired(name = "connect_id")
    @JvmField
    var connectId: String? = ""
    @Autowired(name = "form_type")
    @JvmField
    var formType: String? = ""
    @Autowired(name = "form_edit")
    @JvmField
    var formEdit: Boolean = true

    private var type: FormType? = null

    private val mFormList = ArrayList<FormItem>()
    private var mAdapter: FormAdapter? = null
    private var mPosition: Int? = null
    private var mFileType: FileUtils.TYPE? = null
    private var mForm: FormEntity? = null

    override val layoutId: Int = R.layout.form_activity_main
    override val reload: Boolean = true

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        ActivityUtils.instance.addActivity(this)

        formType = formType ?: ""
        type = FormType.getEnum(formType)
        if (type == null) {
            // FIXME 测试数据
            type = FormType.ADD_NOTICE
        }

        formModelIml = FormModelIml(FormRepository(mContext!!))
    }

    override fun initView() {
        super.initView()

        setSupportActionBar(tb_form)
        tb_form.setNavigationOnClickListener { _ ->
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""

        tv_form.text = type?.title

        rv_form.layoutManager = LinearLayoutManager(mContext)

        mAdapter = FormAdapter(connectId, mFormList)
        // 添加装饰类
        rv_form.addItemDecoration(FormItemDecoration())
        // 设置列表容器
        rv_form.adapter = mAdapter

        btn_form?.visibility = if (formEdit) View.VISIBLE else View.GONE

        btn_form?.setOnClickListener {
            btn_form?.isClickable = false
            btn_form?.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_rectangle_grey)
            // 提交并清数据库
            if (mForm != null) {
                formModelIml.postForm(type!!.name, connectId, mForm!!.id!!)
            } else {
                showToast("提交失败！数据已丢失。")
            }
        }

        // 表单内操作文件监听
        mAdapter?.setOnFileListener(object : FormAdapter.OnFileListener {
            override fun getFile(type: FileUtils.TYPE, position: Int) {
                mFileType = type
                mPosition = position
                when (type) {
                    // TODO 文件选择
                }
                showToast("$type 请接入文件组件")
            }
        })

        formModelIml.dbFormEntity.observe(this, Observer {
            mForm = it

            if (it == null) {
                showToast("获取表单数据失败")
            } else {
                btn_form?.text = mForm!!.submit_name ?: "提交"

                mFormList.clear()
                val formItemList = arrayListOf<FormItem>()
                for (item in mForm!!.form_item!!) {
                    if (item.form_visible) formItemList.add(item)
                }
                mFormList.addAll(formItemList)
                mAdapter?.notifyDataSetChanged()
            }
        })

        formModelIml.postFormStatus.observe(this, Observer {
            PopupWindowUtils.showTextOk(mContext!!, it.message, object : OnTListener<Boolean> {
                override fun getItem(item: Boolean) {
                    if (!it.pass) {
                        showToast("提交失败，请重试")
                        btn_form?.isClickable = true
                        btn_form?.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_rectangle_primary)
                    }
                }
            })
        })

        formModelIml.dbDeleteForm.observe(this, Observer {
            finishThis()
        })
    }

    override fun getData() {
        if (mForm != null) {
            formModelIml.getFormByDB(mForm!!.id!!)
        } else {
            formModelIml.getForm(type!!.name, connectId)
        }
    }

    override fun onBackPressed() {
        if (formEdit) {
            PopupWindowUtils.showTextOk(mContext!!, "要放弃填写么？离开将不会保存您的数据。", object : OnTListener<Boolean> {
                override fun getItem(item: Boolean) {
                    if (item) {
                        finishThis()
                    }
                }
            })
        } else {
            formModelIml.deleteForm(mForm!!)
        }
    }
}
