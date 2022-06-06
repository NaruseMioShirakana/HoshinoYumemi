package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.*

object HoshinoYumemiTencentCloudApiConfig : ReadOnlyPluginConfig("HoshinoYumemi_TencentCloudApiConfig") {
    @ValueName("SecretId")
    @ValueDescription("腾讯云SecretId")
    val HoshinoYumemiSecretId: String by value("SecretId")
    @ValueName("SecretKey")
    @ValueDescription("腾讯云SecretKey")
    val HoshinoYumemiSecretKey: String by value("SecretKey")
}