package qsos.lib.base.data.play

import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : 预览图片列表数据
 */
data class FileListData(
        var position: Int = 0,
        var imageList: ArrayList<FileData>
)

/**
 * @author : 华清松
 * @description : 预览图片数据
 */
data class FileData(
        /**名称*/
        var name: String?,
        /**链接*/
        var url: String?
) : BaseEntity