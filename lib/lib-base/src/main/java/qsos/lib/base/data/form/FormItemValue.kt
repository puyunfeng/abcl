package qsos.lib.base.data.form

import androidx.room.Ignore
import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : 通用表单子项值集实体类
 */
class FormItemValue : BaseEntity {
    constructor()
    constructor(limit_max: Int) {
        this.limit_max = limit_max
    }

    /*值的最小数量*/
    var limit_min: Int? = 0
    /*值的最大数量*/
    var limit_max: Int? = 0
    /*值限制，选用户的时候，可能为角色*/
    var limit_type: String? = ""

    @Ignore
    var values: ArrayList<Value>? = null
}