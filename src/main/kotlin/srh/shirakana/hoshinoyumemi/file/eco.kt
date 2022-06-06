package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import srh.shirakana.hoshinoyumemi.file.HoshinoYumemiReplyList.provideDelegate

public object HoshinoYumemiMoney : AutoSavePluginData("HoshinoYumemiMoney") {

    @ValueName("Money")
    @ValueDescription("金钱列表")
    val HoshinoYumemiNoMoney: MutableMap<Long,Double> by value()

}

