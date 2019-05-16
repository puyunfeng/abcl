package qsos.base.chat.view.widget.emoji

import android.graphics.Bitmap
import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : 表情实体
 */
data class EmojiEntity(
        var name: CharSequence,
        var icon: Bitmap?
) : BaseEntity