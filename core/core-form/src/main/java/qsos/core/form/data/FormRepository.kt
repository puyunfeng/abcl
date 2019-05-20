package qsos.core.form.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.db.FormDatabase
import qsos.core.form.utils.FormVerifyUtils
import qsos.lib.base.data.form.*
import qsos.lib.base.data.http.ApiException
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.ApiObserver
import qsos.lib.netservice.ObservableService
import qsos.lib.netservice.file.BaseRepository
import timber.log.Timber

/**
 * @author : 华清松
 * @description : 表单数据获取
 */
@SuppressLint("CheckResult")
class FormRepository(private val mContext: Context) : IFormModel, BaseRepository() {

    /**NOTICE 数据库操作*/

    override fun getFormByDB(formId: Long) {
        FormDatabase.getInstance(mContext).formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    it.form_item = FormDatabase.getInstance(mContext).formItemDao.getFormItemByFormId(it.id!!)
                    it.form_item?.forEach { formItem ->
                        formItem.form_item_value!!.values = arrayListOf()
                        formItem.form_item_value!!.values!!.addAll(FormDatabase.getInstance(mContext).formItemValueDao.getValueByFormItemId(formItem.id!!))
                        Timber.tag("数据库").i("查询formItem={formItem.id}下Value列表：${Gson().toJson(formItem.form_item_value!!.values)}")
                    }
                }.observeOn(Schedulers.io())
                .subscribe(
                        {
                            dbFormEntity.postValue(it)
                        },
                        {
                            dbFormEntity.postValue(null)
                        }
                )
    }

    override fun insertForm(form: FormEntity) {
        Observable.create<FormEntity> {
            val id = FormDatabase.getInstance(mContext).formDao.insert(form)
            form.id = id
            form.form_item?.forEach { formItem ->
                formItem.form_id = id
                insertFormItem(formItem)
            }
            if (form.id != null) {
                it.onNext(form)
            } else {
                it.onError(Exception("数据插入失败"))
            }
        }.subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            getFormByDB(it.id!!)
                        },
                        {
                            it.printStackTrace()
                            dbFormEntity.postValue(null)
                        }
                )
    }

    override fun insertFormItem(formItem: FormItem) {
        val formItemId = FormDatabase.getInstance(mContext).formItemDao.insert(formItem)
        formItem.form_item_value!!.values?.forEach {
            it.form_item_id = formItemId
            insertValue(it)
        }
    }

    override fun insertValue(formItemValue: Value) {
        FormDatabase.getInstance(mContext).formItemValueDao.insert(formItemValue)
    }

    override fun deleteForm(form: FormEntity) {
        Observable.create<Boolean> {
            FormDatabase.getInstance(mContext).formDao.delete(form)
            it.onNext(true)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dbDeleteForm.postValue(it)
                }
    }

    override fun getFormItemByDB(formItemId: Long) {
        FormDatabase.getInstance(mContext).formItemDao.getFormItemByIdF(formItemId)
                .observeOn(Schedulers.io())
                .map {
                    it.form_item_value!!.values = arrayListOf()
                    it.form_item_value!!.values?.addAll(FormDatabase.getInstance(mContext).formItemValueDao.getValueByFormItemId(formItemId))
                    it
                }.observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dbFormItem.postValue(it)
                }
    }

    override fun updateValue(value: Value) {
        Observable.create<Boolean> {
            FormDatabase.getInstance(mContext).formItemValueDao.update(value)
            it.onNext(true)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateValueStatus.postValue(it)
                }
    }

    override fun postForm(formType: String, connectId: String?, formId: Long) {
        FormDatabase.getInstance(mContext).formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        {
                            val verify = FormVerifyUtils.verify(it)

                            if (verify.pass) {
                                postForm(formType, connectId, it)
                            } else {
                                postFormStatus.postValue(verify)
                            }
                        },
                        {
                            postFormStatus.postValue(FormVerifyUtils.Verify(false, "提交失败 $it"))
                        }
                )

    }

    /**NOTICE 网络请求操作*/

    override fun getForm(formType: String, connectId: String?) {
        ObservableService.setObservable(
                ApiEngine.createService(ApiForm::class.java).getForm(formType, connectId)
        ).subscribe(
                object : ApiObserver<FormEntity>() {

                    override fun onNext(data: FormEntity) {
                        super.onNext(data)
                        if (data.form_item == null) {
                            dbFormEntity.postValue(null)
                        } else {
                            insertForm(data)
                        }
                    }

                    override fun onError(ex: ApiException) {
                        super.onError(ex)
                        // fixme 测试假数据
                        val form: FormEntity = when (formType) {
                            FormType.ADD_NOTICE.name -> getTestData()
                            else -> getTestData()
                        }
                        if (form.form_item == null) {
                            dbFormEntity.postValue(null)
                        } else {
                            insertForm(form)
                        }
                    }
                }
        )
    }

    /**将从数据库中查出的表单，提交表单到服务器*/
    private fun postForm(formType: String, connectId: String?, form: FormEntity) {
        ObservableService.setObservable(
                ApiEngine.createService(ApiForm::class.java)
                        .postForm(formType, connectId, form)
        ).subscribe(
                object : ApiObserver<String>() {

                    override fun onNext(data: String) {
                        super.onNext(data)
                        postFormStatus.postValue(FormVerifyUtils.Verify(true, "提交成功"))
                        // NOTICE 提交成功后清除表单
                        deleteForm(form)
                    }

                    override fun onError(ex: ApiException) {
                        super.onError(ex)
                        postFormStatus.postValue(FormVerifyUtils.Verify(false, "提交失败 $ex"))
                    }
                }
        )
    }

    override fun getUsers(connectId: String?, formItem: FormItem, key: String?) {
        ObservableService.setObservable(
                ApiEngine.createService(ApiForm::class.java)
                        .getUsers(null, formItem.form_item_value?.limit_type, key)
        ).subscribe(
                object : ApiObserver<List<Value>>() {

                    override fun onNext(data: List<Value>) {
                        super.onNext(data)
                        val newUsers = ArrayList<FormUserEntity>()
                        data.forEach { value ->
                            var cb = false
                            val oldUsers = dbFormItem.value?.form_item_value?.values
                            oldUsers!!.forEach { user ->
                                if (user.user_phone == value.user_phone) {
                                    cb = true
                                }
                            }
                            val user = FormUserEntity(
                                    formItem.id,
                                    value.user_id,
                                    value.user_name,
                                    value.user_header,
                                    value.user_phone,
                                    cb,
                                    value.limit_edit
                            )
                            newUsers.add(user)
                        }
                        userList.postValue(newUsers)
                    }

                    override fun onError(ex: ApiException) {
                        super.onError(ex)
                        userList.postValue(null)
                    }
                }
        )
    }

    // 获取表单数据结果
    val dbFormEntity = MutableLiveData<FormEntity?>()

    // 获取表单项数据结果
    val dbFormItem = MutableLiveData<FormItem>()

    // 删除表单数据结果
    val dbDeleteForm = MutableLiveData<Boolean>()

    // 用户列表数据结果
    val userList = MutableLiveData<List<FormUserEntity>?>()

    // 提交表单数据结果
    val postFormStatus = MutableLiveData<FormVerifyUtils.Verify>()

    // 更新列表项值数据结果
    val updateValueStatus = MutableLiveData<Boolean>()

    companion object {
        fun getTestData(): FormEntity {
            val form = FormEntity()
            form.desc = "测试表单"
            form.submit_name = "保存"
            form.title = "测试表单"

            val formItemList = arrayListOf<FormItem>()

            /*输入*/
            val formItem1 = FormItem()
            formItem1.form_item_type = 1
            formItem1.form_item_hint = "请输入您的想法"
            formItem1.form_item_key = "想法"
            formItem1.form_item_required = true

            val itemValue1 = FormItemValue()
            itemValue1.limit_min = 5
            itemValue1.limit_max = 100

            val values1 = arrayListOf<Value>()
            val value1 = Value()
            value1.input_value = ""
            values1.add(value1)
            itemValue1.values = values1
            formItem1.form_item_value = itemValue1
            formItemList.add(formItem1)

            /*单选*/
            val formItem2 = FormItem()
            formItem2.form_item_type = 2
            formItem2.form_item_hint = "请输入选择等级"
            formItem2.form_item_key = "通知等级"
            formItem2.form_item_required = true

            val itemValue2 = FormItemValue()
            itemValue2.limit_min = 1
            itemValue2.limit_max = 1

            val values2 = arrayListOf<Value>()
            val value2 = Value()
            value2.ck_name = "一级"
            value2.ck_value = "1"
            value2.ck_check = true
            values2.add(value2)
            val value22 = Value()
            value22.ck_name = "二级"
            value22.ck_value = "2"
            value22.ck_check = false
            values2.add(value22)
            itemValue2.values = values2
            formItem2.form_item_value = itemValue2
            formItemList.add(formItem2)



            form.form_item = formItemList
            return form
        }
    }
}