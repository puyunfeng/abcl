package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.view.hodler.*
import qsos.core.form.view.widget.dialog.BottomDialogUtils
import qsos.core.form.view.widget.dialog.OnDateListener
import qsos.core.form.view.widget.dialog.Operation
import qsos.core.form.view.widget.pop.PopupWindowUtils
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.routepath.FormPath
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.file.FileUtils
import java.util.*

/**
 * @author : 华清松
 * @desc : 表单列表容器
 */
@SuppressLint("CheckResult")
class FormAdapter(connectId: String?, findItems: ArrayList<FormItem>) : BaseAdapter<FormItem>(findItems) {

    private val mConnectId = connectId

    interface OnFileListener {
        fun getFile(type: FileUtils.TYPE, position: Int)
    }

    private var fileListener: OnFileListener? = null

    fun setOnFileListener(listener: OnFileListener) {
        this.fileListener = listener
    }

    private var backListener: OnTListener<Boolean>? = null

    override fun getHolder(view: View, viewType: Int): BaseHolder<FormItem> {
        when (viewType) {
            /*文本*/
            R.layout.form_item_text -> return ItemFormTextHolder(view)
            /*输入*/
            R.layout.form_item_input -> return ItemFormInputHolder(view)
            /*选项*/
            R.layout.form_item_check -> return ItemFormCheckHolder(view)
            /*时间*/
            R.layout.form_item_time -> return ItemFormTimeHolder(view)
            /*人员*/
            R.layout.form_item_users -> return ItemFormUserHolder(view)
            /*附件*/
            R.layout.form_item_file -> return ItemFormFileHolder(view)
            /*位置*/
            R.layout.form_item_location -> return ItemFormInputHolder(view)
            /*分数*/
            R.layout.form_item_score -> return ItemFormInputHolder(view)
            /*其它*/
            else -> return ItemFormTextHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (data[position].form_item_type) {
            /*文本*/
            0 -> return R.layout.form_item_text
            /*输入*/
            1 -> return R.layout.form_item_input
            /*选项*/
            2 -> return R.layout.form_item_check
            /*时间*/
            3 -> return R.layout.form_item_time
            /*人员*/
            4 -> return R.layout.form_item_users
            /*附件*/
            5 -> return R.layout.form_item_file
            /*位置*/
            6 -> return R.layout.form_item_location
            /*分数*/
            7 -> return R.layout.form_item_score
        }
        return R.layout.form_item_text
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        if (data[position].form_item_status == 0) {
            return
        }

        when (view.id) {
            /**表单项提示*/
            R.id.item_form_key -> {
                PopupWindowUtils.showTextOk(mContext!!, data[position].form_item_hint!!, null)
            }
            /**选择时间*/
            R.id.item_form_time -> {
                var values = data[position].form_item_value?.values
                values = values ?: arrayListOf()
                val size = values.size
                if (size == 1) {
                    val date = values[0].limit_type

                    var showDay = true
                    if ("yyyy-MM-dd HH:mm" == date) {
                        showDay = true
                    } else if ("yyyy-MM-dd" == date) {
                        showDay = true
                    }
                    backListener?.getItem(false)
                    BottomDialogUtils.showRealDateChoseView(view.context,
                            "yyyy-MM-dd HH:mm" == date, showDay,
                            null, null, Date(values[0].time), object : OnDateListener {
                        override fun setDate(type: Int?, date: Date?) {
                            backListener?.getItem(true)
                            if (date != null) {
                                data[position].form_item_value!!.values!![0].time = date.time

                                Completable.fromAction {
                                    FormDatabase.getInstance(mContext!!).formItemValueDao.update(data[position].form_item_value!!.values!!)
                                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                        {
                                            notifyItemChanged(position)
                                        },
                                        {
                                            ToastUtils.showToast(mContext!!, "选择失败")
                                            notifyItemChanged(position)
                                        }
                                )
                            }
                        }
                    }

                    )
                } else if (size == 2) {
                    val date1 = values[0].limit_type
                    val date2 = values[1].limit_type
                    var showDay1 = true
                    if ("yyyy-MM-dd HH:mm" == date1) {
                        showDay1 = true
                    } else if ("yyyy-MM-dd" == date1) {
                        showDay1 = true
                    }
                    var showDay2 = true
                    if ("yyyy-MM-dd HH:mm" == date1) {
                        showDay2 = true
                    } else if ("yyyy-MM-dd" == date1) {
                        showDay2 = true
                    }
                    backListener?.getItem(false)
                    BottomDialogUtils.showRealDateChoseView(view.context,
                            "yyyy-MM-dd HH:mm" == date1, showDay1,
                            null, null, Date(values[0].time),
                            object : OnDateListener {
                                override fun setDate(type: Int?, date: Date?) {
                                    if (date != null) {
                                        data[position].form_item_value!!.values!![0].time = date.time
                                        BottomDialogUtils.showRealDateChoseView(view.context, "yyyy-MM-dd HH:mm" == date2,
                                                showDay2,
                                                Date(values[0].time), null, Date(values[1].time),
                                                object : OnDateListener {
                                                    override fun setDate(type: Int?, date: Date?) {
                                                        backListener?.getItem(true)
                                                        if (date != null) {
                                                            data[position].form_item_value!!.values!![1].time = date.time
                                                            Completable.fromAction {
                                                                FormDatabase.getInstance(mContext!!).formItemValueDao.update(data[position].form_item_value!!.values!!)
                                                            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                                                    {
                                                                        notifyItemChanged(position)
                                                                    },
                                                                    {
                                                                        ToastUtils.showToast(mContext!!, "选择失败")
                                                                        notifyItemChanged(position)
                                                                    }
                                                            )
                                                        }
                                                    }

                                                })

                                    }
                                }

                            }
                    )
                }

            }
            /**表单输入项*/
            R.id.item_form_input -> {
                ARouter.getInstance().build(FormPath.ITEM_INPUT)
                        .withLong("item_id", data[position].id!!)
                        .navigation()
            }
            /**选择人员*/
            R.id.tv_item_form_users_size -> {
                ARouter.getInstance().build(FormPath.ITEM_USERS)
                        .withLong("item_id", data[position].id!!)
                        .withString("connect_id", mConnectId)
                        .navigation()
            }
            /**选择附件*/
            R.id.tv_item_form_files_size -> {
                BottomDialogUtils.setBottomChoseListView(mContext!!, BottomDialogUtils.fileOperation, object : OnTListener<Operation> {
                    override fun getItem(item: Operation) {
                        fileListener?.getFile(item.`val` as FileUtils.TYPE, position)
                    }
                })
            }
            /**选择选项*/
            R.id.form_item_check -> {
                // 单选
                if (data[position].form_item_value!!.limit_max == 1) {
                    val operations = arrayListOf<Operation>()
                    val values = data[position].form_item_value!!.values
                    values!!.forEach {
                        val operation = Operation()
                        operation.key = it.ck_name
                        operation.`val` = it.id
                        operation.isCheck = it.ck_check
                        operations.add(operation)
                    }
                    backListener?.getItem(false)
                    BottomDialogUtils.setBottomChoseListView(mContext!!, operations, object : OnTListener<Operation> {
                        override fun getItem(item: Operation) {
                            backListener?.getItem(true)
                            data[position].form_item_value!!.values!!.forEach {
                                it.ck_check = it.id == item.`val`
                            }
                            if (data[position].form_item_value!!.values!!.isNotEmpty()) {
                                Completable.fromAction {
                                    FormDatabase.getInstance(mContext!!).formItemValueDao.update(data[position].form_item_value!!.values!!)
                                }.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                {
                                                    notifyItemChanged(position)
                                                },
                                                {
                                                    ToastUtils.showToast(mContext!!, "选择失败")
                                                    notifyItemChanged(position)
                                                }
                                        )
                            } else {
                                notifyItemChanged(position)
                            }
                        }
                    })
                } else {
                    // 多选
                    val operations = arrayListOf<Operation>()
                    val values = data[position].form_item_value!!.values
                    values!!.forEach {
                        val operation = Operation()
                        operation.key = it.ck_name
                        operation.`val` = it.id
                        operation.isCheck = it.ck_check
                        operations.add(operation)
                    }
                    backListener?.getItem(false)
                    BottomDialogUtils.setBottomSelectListView(mContext!!, data[position].form_item_key, operations, object : OnTListener<List<Operation>> {
                        override fun getItem(item: List<Operation>) {
                            backListener?.getItem(true)
                            data[position].form_item_value!!.values!!.forEach { value ->
                                item.forEach {
                                    if (value.id == it.`val`) {
                                        value.ck_check = it.isCheck
                                    }
                                }
                            }
                            if (data[position].form_item_value!!.values!!.isNotEmpty()) {
                                Completable.fromAction {
                                    FormDatabase.getInstance(mContext!!).formItemValueDao.update(data[position].form_item_value!!.values!!)
                                }.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                {
                                                    notifyItemChanged(position)
                                                },
                                                {
                                                    ToastUtils.showToast(mContext!!, "选择失败")
                                                    notifyItemChanged(position)
                                                }
                                        )
                            } else {
                                notifyItemChanged(position)
                            }
                        }

                    })
                }
            }
            else -> {

            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }
}
