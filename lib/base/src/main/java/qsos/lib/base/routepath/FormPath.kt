package qsos.lib.base.routepath

/**
 * @author : 华清松
 * @description : 表单模块页面路由
 */
object FormPath {

    const val FORM = "FORM"
    /*表单页*/
    const val MAIN = "/$FORM/form"
    /*表单项录入页*/
    const val ITEM_INPUT = "/$FORM/form/item/input"
    /*表单项用户页*/
    const val ITEM_USERS = "/$FORM/form/item/user"
    /*审核协调选择用户*/
    const val ITEM_USER = "/$FORM/form/item/check"

    const val choseUserCode = 20001
}