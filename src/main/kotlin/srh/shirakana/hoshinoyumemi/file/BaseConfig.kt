package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object HoshinoYumemiConfig : ReadOnlyPluginConfig("HoshinoYumemiConfig") {
    @ValueName("Name")
    @ValueDescription("机器人的名字")
    val HoshinoYumemiNoName: String by value("梦美")
}