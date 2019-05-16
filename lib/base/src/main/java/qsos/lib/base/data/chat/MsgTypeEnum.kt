package qsos.lib.base.data.chat

/**
 * @author : 华清松
 * @description : 消息类型枚举
 */
enum class MsgTypeEnum constructor(val value: Int, val desc: String) {
    UN_KNOW(-1, "未知类型"),
    TEXT(1, "文本"),
    IMAGE(2, "图片"),
    AUDIO(3, "语音"),
    VIDEO(4, "视频"),
    FILE(6, "文件"),
    INSTR(7, "指令"),
    CARD(8, "名片"),
    NOTICE(10, "提醒"),
    LOCATION(5, "位置");
}