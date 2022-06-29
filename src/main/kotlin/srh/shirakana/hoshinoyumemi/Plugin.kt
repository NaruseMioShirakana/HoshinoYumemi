package srh.shirakana.hoshinoyumemi

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.registerTo
import net.mamoe.mirai.utils.info
import srh.shirakana.hoshinoyumemi.HoshiniYumemi.reload
import srh.shirakana.hoshinoyumemi.command.*
import srh.shirakana.hoshinoyumemi.file.*

object HoshiniYumemi : KotlinPlugin(
    JvmPluginDescription(
        id = "srh.shirakana.hoshinoyumemi.plugin",
        name = "功能性机器人",
        version = "0.0.3",
    ) {
        author("藤原白叶")
    }
) {
    override fun onEnable() {
        HoshinoYumemiTencentCloudDisabledGroups.reload()
        HoshinoYumemiTencentCloudApiConfig.reload()
        HoshinoYumemiReplyList.reload()
        HoshinoYumemiKouKann.reload()
        HoshinoYumemiUser.reload()
        HoshinoYumemiMoney.reload()
        HoshinoYumemiBlackList.reload()
        HoshinoYumemiSwitch.reload()
        HoshinoYumemiSaucenaoApiConfig.reload()
        HoshinoYumemiShop.reload()
        HoshinoYumemiConfig.reload()
        HoshinoYumemiCourse.reload()

        ShirakanaEventListener.registerTo(globalEventChannel())

        HoshinoYumemiSeTuCommand.register()
        HoshinoYumemiKouKannCommand.register()
        HoshinoYumemiMoneyCommand.register()
        HoshinoYumemiReplyListCommand.register()
        HoshinoYumemiTencentCloudAPI.register()
        HoshinoYumemiUserCommand.register()
        HoshinoYumemiWorkCommand.register()

        logger.info { "Plugin 功能性机器人 loaded" }
    }

    override fun onDisable() {
        HoshinoYumemiKouKannCommand.unregister()
        HoshinoYumemiMoneyCommand.unregister()
        HoshinoYumemiReplyListCommand.unregister()
        HoshinoYumemiTencentCloudAPI.unregister()
        HoshinoYumemiUserCommand.unregister()
        HoshinoYumemiSeTuCommand.unregister()
        HoshinoYumemiWorkCommand.unregister()
    }
}