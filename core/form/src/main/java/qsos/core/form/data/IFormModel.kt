package qsos.core.form.data

import qsos.lib.base.data.form.FormEntity
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.Value

/**
 * @author : 华清松
 * @description : 表单操作接口
 */
interface IFormModel {
    /**获取表单数据*/
    fun getForm(formType: String, connectId: String?)

    /**获取表单数据库*/
    fun getFormByDB(formId: Long)

    /**插入表单数据*/
    fun insertForm(form: FormEntity)

    /**插入表单项数据*/
    fun insertFormItem(formItem: FormItem)

    /**插入表单项值数据*/
    fun insertValue(formItemValue: Value)

    /**清除表单数据*/
    fun deleteForm(form: FormEntity)

    /**提交表单数据*/
    fun postForm(formType: String, connectId: String?, formId: Long)

    /**获取表单项数据*/
    fun getFormItemByDB(formItemId: Long)

    /**更新表单项数据*/
    fun updateValue(value: Value)

    /**获取可选用户列表 key 可以是姓名、手机号*/
    fun getUsers(connectId: String?, formItem: FormItem, key: String?)
}