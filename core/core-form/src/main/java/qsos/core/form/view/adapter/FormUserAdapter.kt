package qsos.core.form.view.adapter

import android.view.View
import qsos.core.form.R
import qsos.core.form.view.hodler.UserHolder
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.app.UserEntity

/**
 * @author : 华清松
 * @description : 用户列表容器
 */
class FormUserAdapter(users: ArrayList<UserEntity>) : BaseAdapter<UserEntity>(users) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<UserEntity> {
        return UserHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.form_item_user
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {

    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }
}