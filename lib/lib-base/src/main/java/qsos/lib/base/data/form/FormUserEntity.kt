package qsos.lib.base.data.form

import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : 选择用户实体类
 */
class FormUserEntity : BaseEntity {
    constructor()
    constructor(form_item_id: Long?, user_id: String?, name: String?, head: String?, phone: String?, cb: Boolean, limit: Boolean) {
        this.form_item_id = form_item_id
        this.user_id = user_id
        this.user_name = name
        this.user_phone = phone
        this.user_header = head
        this.user_cb = cb
        this.user_limit = limit
    }

    var id: Int? = null
    var user_name: String? = null
    var user_phone: String? = ""
    var user_header: String? = null
    var user_id: String? = ""
    var user_cb: Boolean = false
    var user_limit: Boolean = false
    /*外键*/
    var form_item_id: Long? = null

}