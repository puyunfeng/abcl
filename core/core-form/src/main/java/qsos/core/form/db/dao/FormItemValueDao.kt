package qsos.core.form.db.dao

import androidx.room.*
import qsos.lib.base.data.form.Value

/**
 * @author : 华清松
 * @description : 表单项值 Dao 层
 */
@Dao
interface FormItemValueDao {

    @Query("SELECT * FROM form_item_value where form_item_id=:form_item_id")
    fun getValueByFormItemId(form_item_id: Long): List<Value>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: Value)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(value: Value)

    @Update
    fun update(value: List<Value>)

    @Delete
    fun delete(value: Value)

    @Query("DELETE FROM form_item_value WHERE form_item_id=:form_item_id AND user_phone=:userPhone")
    fun deleteUserByUserId(form_item_id: Long?, userPhone: String)

    @Query("DELETE FROM form_item_value where form_item_id=:form_item_id")
    fun deleteByFormItemId(form_item_id: Long?)

}