package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import srh.shirakana.hoshinoyumemi.file.HoshinoYumemiKouKann.provideDelegate

public object HoshinoYumemiTencentCloudDisabledGroups : AutoSavePluginData("HoshinoYumemiTencentCloudDisabledGroups") {

    @ValueName("HoshinoYumemiTencentCloudDisabledGroups")
    @ValueDescription("关闭腾讯云NLP的群聊")
    val HoshinoYumemiNoTencentCloudDisabledGroups: MutableSet<Long> by value()

}