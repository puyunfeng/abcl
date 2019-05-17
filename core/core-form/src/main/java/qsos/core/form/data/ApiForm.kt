package qsos.core.form.data

import io.reactivex.Observable
import qsos.lib.base.data.BaseResult
import qsos.lib.base.data.form.FormEntity
import qsos.lib.base.data.form.Value
import retrofit2.http.*

/**
 * @author : 华清松(姓名)
 * @description : 表单接口类
 */
interface ApiForm {

    /**表单-获取业务表单*/
    @GET("/app/form/{type}")
    fun getForm(
            @Path("type") type: String,
            @Query("id") connectId: String?
    ): Observable<BaseResult<FormEntity>>

    /**表单-提交业务表单*/
    @POST("/app/form/{type}")
    fun postForm(
            @Path("type") type: String,
            @Query("id") connectId: String?,
            @Body form: FormEntity
    ): Observable<BaseResult<String>>

    /**表单-获取可选用户列表*/
    @GET("/app/form/users")
    fun getUsers(
            // 关联 ID ，如任务的 ID ，目前需求没有涉及，所以 ID 为空
            @Query("id") connectId: String?,
            // 人员类型，目前可视为人员角色，如 admin
            @Query("type") type: String?,
            // 搜索字段，目前暂未为 用户姓名或手机号
            @Query("key") key: String?
    ): Observable<BaseResult<List<Value>>>

}
