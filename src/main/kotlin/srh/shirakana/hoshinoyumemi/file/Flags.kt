package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import srh.shirakana.hoshinoyumemi.file.HoshinoYumemiReplyList.provideDelegate

public object HoshinoYumemiSwitch : AutoSavePluginData("HoshinoYumemiSwitch") {

    @ValueName("Switch")
    @ValueDescription("开关")
    var HoshinoYumemiNoSwitch: Boolean by value(false)

}