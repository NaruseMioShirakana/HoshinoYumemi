package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import srh.shirakana.hoshinoyumemi.file.HoshinoYumemiKouKann.provideDelegate

public object HoshinoYumemiBlackList : AutoSavePluginData("HoshinoYumemiBlackList") {

    @ValueName("BlackList")
    @ValueDescription("黑名单")
    val HoshinoYumemiNoBlackList: MutableSet<Long> by value()

}