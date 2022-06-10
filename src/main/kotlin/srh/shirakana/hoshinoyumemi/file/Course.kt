package srh.shirakana.hoshinoyumemi.file

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import srh.shirakana.hoshinoyumemi.file.HoshinoYumemiBlackList.provideDelegate

public object HoshinoYumemiCourse : AutoSavePluginData("HoshinoYumemiCourse") {

    @ValueName("Course")
    @ValueDescription("题目")
    val HoshinoYumemiNoCourses: MutableMap<String,MutableSet<Map<String,String>>> by value()

}