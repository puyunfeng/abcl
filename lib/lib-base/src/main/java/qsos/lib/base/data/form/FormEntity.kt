package qsos.lib.base.data.form

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : 通用表单实体类
 */
@Entity(tableName = "form",
        indices = [Index(value = ["id"], unique = true)]
)
class FormEntity : BaseEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    var submitter: Int? = 0
    var scene_type: Int? = 0
    var desc: String? = null
    var title: String? = null
    var submit_time: String? = null
    var submit_name: String? = null

    @Ignore
    var form_item: List<FormItem>? = arrayListOf()

}
