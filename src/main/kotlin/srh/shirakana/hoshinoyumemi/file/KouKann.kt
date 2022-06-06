package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.*

public object HoshinoYumemiKouKann : AutoSavePluginData("HoshinoYumemiKouKann") {

    @ValueName("KouKannList")
    @ValueDescription("好感度列表")
    val HoshinoYumemiNoKouKann: MutableMap<Long,Double> by value()

}