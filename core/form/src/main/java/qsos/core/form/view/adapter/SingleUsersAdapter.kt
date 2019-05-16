package qsos.core.form.view.adapter

import android.content.Intent
import android.view.View
import com.google.gson.Gson
import qsos.core.form.R
import qsos.core.form.view.hodler.FormUsersHolder
import qsos.lib.base.base.BaseActivity
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.FormUserEntity
import qsos.lib.base.routepath.FormPath
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * @description : 表单用户单选列表容器
 */
class SingleUsersAdapter(users: ArrayList<FormUserEntity>) : BaseAdapter<FormUserEntity>(users) {

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
        when (view.id) {
            R.id.form_user_ll -> {
                val activity: BaseActivity = mContext as BaseActivity
                if (data[position].user_id == null) {
                    ToastUtils.showToast(mContext, "人员ID不存在，选择失败")
                } else {
                    val intent = Intent()
                    intent.putExtra("userJson", Gson().toJson(data[position]))
                    activity.setResult(FormPath.choseUserCode, intent)
                    activity.finishThis()
                }
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }
}
