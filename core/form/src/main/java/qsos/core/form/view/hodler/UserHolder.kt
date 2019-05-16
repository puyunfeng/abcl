package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_user.view.*
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.app.UserEntity
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 用户 列表项布局
 */
class UserHolder(itemView: View) : BaseHolder<UserEntity>(itemView) {
    override fun setData(data: UserEntity, position: Int) {
        ImageLoaderUtils.displayHeader(itemView.context, itemView.iv_item_user, data.avatar)
        itemView.tv_item_user.text = "${data.realName}"
    }
}