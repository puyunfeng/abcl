package qsos.core.form.view.adapter

import android.view.View
import kotlinx.android.synthetic.main.form_item_file_item.view.*
import qsos.core.form.R
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.Value
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 图片文件布局
 */
class FormFileHolder(itemView: View) : BaseHolder<Value>(itemView) {

    override fun setData(data: Value, position: Int) {
        ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, R.drawable.file)

        itemView.tv_item_form_file_name.text = data.file_name

        itemView.setOnLongClickListener {
            onLongClick(it)
        }
    }

}