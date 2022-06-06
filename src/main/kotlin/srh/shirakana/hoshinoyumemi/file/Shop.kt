package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

public object HoshinoYumemiShop : AutoSavePluginData("HoshinoYumemiShop") {

    @ValueName("Shop")
    @ValueDescription("商店列表")
    val HoshinoYumemiNoShop: MutableSet<MutableMap<String,Double>> by value()

}