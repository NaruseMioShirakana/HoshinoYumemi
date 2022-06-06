package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object HoshinoYumemiSaucenaoApiConfig : ReadOnlyPluginConfig("HoshinoYumemiSaucenaoApiConfig") {
    @ValueName("SecretKey")
    @ValueDescription("SaucenaoSecretKey")
    val HoshinoYumemiSaucenaoSecretKey: String by value("SecretKey")
}