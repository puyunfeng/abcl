package qsos.core.form.view.widget.dialog

import androidx.annotation.DrawableRes
import qsos.core.form.R
import java.util.*

/**
 * @author 华清松
 * @doc 类说明：底部弹窗操作实体
 */
class Operation {

    @DrawableRes
    var iconId = R.drawable.dot_black

    var key: String? = null
    var position: Int = 0
    var isCheck: Boolean = false
    var `val`: Any? = null

    constructor()

    constructor(iconId: Int, key: String, position: Int) {
        this.iconId = iconId
        this.key = key
        this.position = position
    }

    constructor(iconId: Int, key: String, position: Int, `val`: Any) {
        this.iconId = iconId
        this.key = key
        this.position = position
        this.`val` = `val`
    }

    constructor(position: Int, key: String, `val`: Any) {
        this.key = key
        this.position = position
        this.`val` = `val`
    }

    constructor(iconId: Int, key: String, position: Int, check: Boolean, `val`: Any) {
        this.iconId = iconId
        this.key = key
        this.isCheck = check
        this.position = position
        this.`val` = `val`
    }

    companion object {

        fun createOperation(vararg createOperations: Operation): List<Operation> {
            val operations = ArrayList<Operation>()
            if (createOperations.isNotEmpty()) {
                operations.addAll(Arrays.asList(*createOperations))
            }
            return operations
        }
    }
}
