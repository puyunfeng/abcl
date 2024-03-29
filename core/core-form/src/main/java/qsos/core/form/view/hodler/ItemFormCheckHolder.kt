package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_check.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.FormItem

/**
 * @author : 华清松
 * @description : 表单文本列表项视图
 */
class ItemFormCheckHolder(itemView: View) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        itemView.item_form_key.text = data.form_item_key

        if (data.form_item_value!!.values != null && !data.form_item_value!!.values!!.isEmpty()) {
            var text = ""
            data.form_item_value!!.values!!.forEach { v ->
                if (v.ck_check) {
                    text += v.ck_name + ";"
                }
            }
            itemView.form_item_check.text = text
        }

        itemView.form_item_check.setOnClickListener {
            onClick(itemView.form_item_check)
        }

    }

}
