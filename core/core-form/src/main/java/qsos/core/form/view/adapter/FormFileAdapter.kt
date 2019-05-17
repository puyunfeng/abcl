package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.view.View
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.Value
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * @description : 文件列表容器
 */
class FormFileAdapter(files: ArrayList<Value>)
    : BaseAdapter<Value>(files) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<Value> {
        return FormFileHolder(view).setOnItemLongClickListener(this)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.form_item_file_item
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        // TODO 预览
    }

    @SuppressLint("CheckResult")
    override fun onItemLongClick(view: View, position: Int, obj: Any?) {
        if (!data[position].limit_edit) {
            Completable.fromAction {
                FormDatabase.getInstance(mContext!!).formItemValueDao.delete(data[position])
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                data.removeAt(position)
                                notifyDataSetChanged()
                            },
                            {
                                ToastUtils.showToast(mContext!!, "删除失败")
                                notifyDataSetChanged()
                            }
                    )
        }
    }
}
