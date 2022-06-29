package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

public object HoshinoYumemiDaiXueXi : AutoSavePluginData("HoshinoYumemiDaiXueXi") {

    @ValueName("HoshinoYumemiDaiXueXi")
    @ValueDescription("带学习")
    val HoshinoYumemiNoDaiXueXi: MutableSet<Long> by value()

}