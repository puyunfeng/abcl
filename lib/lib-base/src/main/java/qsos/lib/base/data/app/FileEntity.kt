package qsos.lib.base.data.app

import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : 文件实体类
 */
open class FileEntity : BaseEntity {
    companion object {
        /**根据文件后缀获取文件类型*/
        fun getFileType(extension: String?): FileTypeEnum {
            return when (extension?.toLowerCase()) {
                "png", "jpg", "jpeg", "gif" -> FileTypeEnum.IMAGE
                "mp3", "wav", "raw", "amr" -> FileTypeEnum.AUDIO
                "mp4", "flv", "avi", "3gp", "webm", "ts", "ogv", "m3u8", "asf", "wmv", "rm", "rmvb", "mov", "mkv" -> FileTypeEnum.VIDEO
                "doc", "docx", "pdf", "txt", "ppt", "pptx", "xls", "xlsx" -> FileTypeEnum.WORD
                else -> FileTypeEnum.OTHER
            }
        }
    }

    // 文件id
    var id: String? = null
    // 文件名
    var name: String? = ""
    // 文件地址
    var url: String? = null
    /**文件本地路径*/
    var path: String? = null
    /**文件后缀*/
    var extension: String? = null
    /**文件类型*/
    var type: FileTypeEnum? = getFileType(extension)

    enum class FileTypeEnum(val type: String) {
        IMAGE("图片"),
        VIDEO("视频"),
        AUDIO("音频"),
        WORD("文档"),
        OTHER("其它")
    }
}
