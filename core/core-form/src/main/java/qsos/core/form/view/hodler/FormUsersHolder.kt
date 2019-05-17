package qsos.core.form.view.hodler

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.form_user.view.*
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.FormUserEntity
import qsos.lib.base.utils.image.ImageLoaderUtils
import qsos.core.form.R

/**
 * @author : 华清松
 * @description : 表单用户列表项视图
 */
class FormUsersHolder(itemView: View) : BaseHolder<FormUserEntity>(itemView) {

    private val context: Context = itemView.context

    override fun setData(data: FormUserEntity, position: Int) {

        itemView.form_user_name.text = data.user_name
        itemView.form_user_phone.text = data.user_phone

        itemView.form_user_cb.visibility = if (data.user_cb) View.VISIBLE else View.INVISIBLE
        itemView.form_user_cb.setImageResource(if (data.user_limit) R.drawable.vector_drawable_lock else R.drawable.vector_drawable_check)

        ImageLoaderUtils.displayHeader(context, itemView.form_user_head, data.user_header)

        if (!data.user_limit) {
            itemView.form_user_ll.setOnClickListener {
                onClick(itemView.form_user_ll)
            }
        }

    }

}
