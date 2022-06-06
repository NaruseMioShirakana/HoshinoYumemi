package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.*

public object HoshinoYumemiReplyList : AutoSavePluginData("HoshinoYumemiReplyList") {

    //Map<发送的信息，Map<好感度，Set<回复的信息>>>
    @ValueName("ReplyList")
    @ValueDescription("回复列表")
    val HoshinoYumemiNoReplyList: MutableMap<String,MutableSet<Map<Long,String>>> by value()

}