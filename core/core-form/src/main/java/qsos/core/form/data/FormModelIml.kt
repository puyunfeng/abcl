package qsos.core.form.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import qsos.lib.base.data.form.FormEntity
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.FormUserEntity
import qsos.lib.base.data.form.Value
import qsos.core.form.utils.FormVerifyUtils

/**
 * @author : 华清松
 * @description : 表单数据实现
 */
class FormModelIml(
        private val formRepo: FormRepository
) : ViewModel(), IFormModel {
    override fun getForm(formType: String, connectId: String?) {
        formRepo.getForm(formType, connectId)
    }

    override fun getFormByDB(formId: Long) {
        formRepo.getFormByDB(formId)
    }

    override fun insertForm(form: FormEntity) {
        formRepo.insertForm(form)
    }

    override fun insertFormItem(formItem: FormItem) {
        formRepo.insertFormItem(formItem)
    }

    override fun insertValue(formItemValue: Value) {
        formRepo.insertValue(formItemValue)
    }

    override fun deleteForm(form: FormEntity) {
        formRepo.deleteForm(form)
    }

    override fun postForm(formType: String, connectId: String?, formId: Long) {
        formRepo.postForm(formType, connectId, formId)
    }

    override fun getFormItemByDB(formItemId: Long) {
        formRepo.getFormItemByDB(formItemId)
    }

    override fun updateValue(value: Value) {
        formRepo.updateValue(value)
    }

    override fun getUsers(connectId: String?, formItem: FormItem, key: String?) {
        formRepo.getUsers(connectId, formItem, key)
    }

    // 获取表单数据结果
    val dbFormEntity: LiveData<FormEntity?> = Transformations.map(formRepo.dbFormEntity) { it }

    // 获取表单项数据结果
    val dbFormItem: LiveData<FormItem> = Transformations.map(formRepo.dbFormItem) { it }

    // 删除表单数据结果
    val dbDeleteForm: LiveData<Boolean> = Transformations.map(formRepo.dbDeleteForm) { it }

    // 用户列表数据结果
    val userList: LiveData<List<FormUserEntity>?> = Transformations.map(formRepo.userList) { it }

    // 提交表单数据结果
    val postFormStatus: LiveData<FormVerifyUtils.Verify> = Transformations.map(formRepo.postFormStatus) { it }

    // 更新表单列表项值数据结果
    val updateValueStatus: LiveData<Boolean> = Transformations.map(formRepo.updateValueStatus) { it }

}