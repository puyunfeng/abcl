package qsos.core.form.view.hodler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.form_item_users.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.view.adapter.FormUserAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.app.UserEntity
import qsos.lib.base.data.form.FormItem

/**
 * @author : 华清松
 * @desc : 表单用户列表项视图
 */
class ItemFormUserHolder(itemView: View) : BaseHolder<FormItem>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun setData(data: FormItem, position: Int) {
        itemView.item_form_key.text = "${data.form_item_key}"

        if (data.form_item_value?.values != null) {
            itemView.tv_item_form_users_size.text = "${data.form_item_value!!.values?.size}\t人"
            itemView.rv_item_form_users.layoutManager = GridLayoutManager(itemView.context, 5) as RecyclerView.LayoutManager?
            val users = arrayListOf<UserEntity>()
            data.form_item_value?.values!!.forEach {
                users.add(UserEntity(it.user_name, "${it.user_phone}", it.user_header))
            }
            itemView.rv_item_form_users.adapter = FormUserAdapter(users)
        }

        /*监听*/
        itemView.item_form_key.setOnClickListener {
            onClick(itemView.item_form_key)
        }
        itemView.tv_item_form_users_size.setOnClickListener {
            onClick(itemView.tv_item_form_users_size)
        }
    }

}
