package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.view.hodler.FormUsersHolder
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.data.form.FormUserEntity
import qsos.lib.base.data.form.Value
import qsos.lib.base.utils.ToastUtils
import java.util.*

/**
 * @author : 华清松
 * @description : 表单用户列表容器
 */
class FormUsersAdapter(users: ArrayList<FormUserEntity>) : BaseAdapter<FormUserEntity>(users) {

    private var chose = 0
    private var limitMin: Int? = 0
    private var limitMax: Int? = 0
    private var mOnChoseUserNum: OnTListener<Int>? = null

    fun setLimit(chose: Int, limitMin: Int?, limitMax: Int?) {
        this.chose = chose
        this.limitMin = limitMin
        this.limitMax = limitMax
    }

    fun setOnChoseListener(onChoseUserNum: OnTListener<Int>) {
        this.mOnChoseUserNum = onChoseUserNum
    }

    /**选中当前列表所有用户或取消当前列表已选用户的选中*/
    @SuppressLint("CheckResult")
    fun changeAllChose(choseAll: Boolean) {
        Completable.fromAction {
            if (choseAll) {
                data.forEach {
                    if (!it.user_cb) {
                        chose++
                        it.user_cb = true
                        FormDatabase.getInstance(mContext!!).formItemValueDao.insert(
                                Value(
                                        it.form_item_id,
                                        it.user_name,
                                        it.user_phone,
                                        it.user_header,
                                        it.user_id
                                )
                        )
                    }
                }
            } else {
                data.forEach {
                    if (it.user_cb) {
                        chose--
                        it.user_cb = false
                        FormDatabase.getInstance(mContext!!).formItemValueDao.deleteUserByUserId(it.form_item_id!!, it.user_phone!!)
                    }
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mOnChoseUserNum?.getItem(chose)
                    notifyDataSetChanged()
                }
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<FormUserEntity> {
        return FormUsersHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.form_user
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {

    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.form_user_ll -> {
                if (limitMax == 1) {
                    /*单选*/
                    Completable.fromAction {
                        FormDatabase.getInstance(mContext!!).formItemValueDao.deleteByFormItemId(data[position].form_item_id)
                        FormDatabase.getInstance(mContext!!).formItemValueDao.insert(
                                Value(
                                        data[position].form_item_id,
                                        data[position].user_name,
                                        data[position].user_phone,
                                        data[position].user_header,
                                        data[position].user_id
                                )
                        )
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                chose = 1
                                for ((index, user) in data.withIndex()) {
                                    user.user_cb = index == position
                                }
                                notifyDataSetChanged()
                                if (chose == limitMax && limitMax == 1) {
                                    (mContext as Activity).finish()
                                }
                            }
                } else {
                    /*多选*/
                    if (data[position].user_cb) {
                        Completable.fromAction {
                            FormDatabase.getInstance(mContext!!).formItemValueDao.deleteUserByUserId(data[position].form_item_id!!, data[position].user_phone!!)
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    chose--
                                    data[position].user_cb = false
                                    mOnChoseUserNum?.getItem(chose)
                                    notifyItemChanged(position)
                                }
                    } else {
                        if (chose == limitMax) {
                            ToastUtils.showToast(mContext!!, "最多选择 $limitMax 人")
                            return
                        }
                        Completable.fromAction {
                            FormDatabase.getInstance(mContext!!).formItemValueDao.insert(
                                    Value(
                                            data[position].form_item_id,
                                            data[position].user_name,
                                            data[position].user_phone,
                                            data[position].user_header,
                                            data[position].user_id
                                    )
                            )
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    chose++
                                    data[position].user_cb = true
                                    mOnChoseUserNum?.getItem(chose)
                                    notifyItemChanged(position)
                                }
                    }
                }
            }
        }
    }

}
