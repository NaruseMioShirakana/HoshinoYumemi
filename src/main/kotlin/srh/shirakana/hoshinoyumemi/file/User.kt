package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.*

public object HoshinoYumemiUser : AutoSavePluginData("HoshinoYumemiUser") {

    @ValueName("UserStatus")
    @ValueDescription("用户状态")
    val HoshinoYumemiNoUser: MutableMap<Long,Boolean> by value()

    @ValueName("SpecialUser")
    @ValueDescription("特殊用户列表")
    val HoshinoYumemiNoSpecialUser: MutableSet<Long> by value(mutableSetOf(1751842477))

}