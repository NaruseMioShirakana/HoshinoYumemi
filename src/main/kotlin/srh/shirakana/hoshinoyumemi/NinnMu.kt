package srh.shirakana.hoshinoyumemi

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature
import com.tencentcloudapi.common.Credential
import com.tencentcloudapi.common.exception.TencentCloudSDKException
import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.HttpProfile
import com.tencentcloudapi.nlp.v20190408.NlpClient
import com.tencentcloudapi.nlp.v20190408.models.ChatBotRequest
import com.tencentcloudapi.nlp.v20190408.models.TextSimilarityRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import srh.shirakana.hoshinoyumemi.command.HoshinoYumemiKouKannCommand.buildSignImage
import srh.shirakana.hoshinoyumemi.file.*
import java.io.File
import java.math.RoundingMode
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


var userJobs = BotJobs()
var userS = UserWorks()

public object ShirakanaEventListener : SimpleListenerHost() {
    @EventHandler
    @OptIn(ExperimentalSerializationApi::class)
    internal suspend fun BotOnlineEvent.handle0() {
        val newDire = File("Yumemi")
        if(!newDire.exists()){
            newDire.mkdir()
        }
        val userDataFile = File("data/srh.shirakana.hoshinoyumemi.plugin/Users.json")
        val jobsDataFile = File("data/srh.shirakana.hoshinoyumemi.plugin/Jobs.json")
        if(!userDataFile.exists()) withContext(Dispatchers.IO) {
            userDataFile.createNewFile()
            userDataFile.writeText("{}")
        }
        if(!jobsDataFile.exists()) withContext(Dispatchers.IO) {
            jobsDataFile.createNewFile()
            jobsDataFile.writeText("{}")
        }
        userS = Json.decodeFromString(String(withContext(Dispatchers.IO) {
            Files.readAllBytes(Paths.get(userDataFile.path))
        },UTF_8))
        userJobs = Json.decodeFromString(String(withContext(Dispatchers.IO) {
            Files.readAllBytes(Paths.get(jobsDataFile.path))
        },UTF_8))
        for(group in bot.groups){
            for(member in group.members){
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] ==null){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 0.0
                }
                if(HoshinoYumemiUser.HoshinoYumemiNoUser[member.id]==null){
                    HoshinoYumemiUser.HoshinoYumemiNoUser[member.id] = false
                }
                if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]==null){
                    HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id] = 0.0
                }
                if(userS[member.id]==null){
                    userS.userdata.add(UserWorks.UserWorksData(member.id,0,"null",false,"",false))
                }
            }
        }

        val t1 = Timer()
        val startCalendar = Calendar.getInstance()
        startCalendar[Calendar.HOUR_OF_DAY] = 0 // 控制时
        startCalendar[Calendar.MINUTE] = 0 // 控制分
        startCalendar[Calendar.SECOND] = 0 // 控制秒
        val startTime = startCalendar.time
        t1.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                for(key in HoshinoYumemiUser.HoshinoYumemiNoUser.keys){
                    HoshinoYumemiUser.HoshinoYumemiNoUser[key] = false
                }
                for(userTmp in userS.userdata){
                    userTmp.state=false
                }
                val dire = File("Yumemi")
                if (dire.exists()){
                    for(file in dire.listFiles()!!){
                        file.delete()
                    }
                }

                val userSJson = Json.encodeToString(userS)
                val userJobsJson = Json.encodeToString(userJobs)
                val jsonObjectUserS: JSONObject = JSONObject.parseObject(userSJson)
                val formatStrUserS: String = JSON.toJSONString(
                    jsonObjectUserS, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat
                )
                userDataFile.writeText(formatStrUserS)
                val jsonObjectUserJobs: JSONObject = JSONObject.parseObject(userJobsJson)
                val formatStrUserJobs: String = JSON.toJSONString(
                    jsonObjectUserJobs, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat
                )
                jobsDataFile.writeText(formatStrUserJobs)

                println("好感度及每日状态重载完毕")
            }
        }, startTime, 1000*24*60*60)
        println("开启定时任务：好感度及每日状态重载")
    }
    @EventHandler
    @OptIn(ExperimentalSerializationApi::class)
    internal suspend fun BotOfflineEvent.handle0() {
        val userDataFile = File("data/srh.shirakana.hoshinoyumemi.plugin/Users.json")
        val jobsDataFile = File("data/srh.shirakana.hoshinoyumemi.plugin/Jobs.json")

        val userSJson = Json.encodeToString(userS)
        val userJobsJson = Json.encodeToString(userJobs)

        val jsonObjectUserS: JSONObject = JSONObject.parseObject(userSJson)
        val formatStrUserS: String = JSON.toJSONString(
            jsonObjectUserS, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteDateUseDateFormat
        )
        userDataFile.writeText(formatStrUserS)

        val jsonObjectUserJobs: JSONObject = JSONObject.parseObject(userJobsJson)
        val formatStrUserJobs: String = JSON.toJSONString(
            jsonObjectUserJobs, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteDateUseDateFormat
        )
        jobsDataFile.writeText(formatStrUserJobs)
    }
    @EventHandler
    internal suspend fun MemberJoinEvent.handle0() {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] ==null){
            HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 0.0
        }
        if(HoshinoYumemiUser.HoshinoYumemiNoUser[member.id]==null){
            HoshinoYumemiUser.HoshinoYumemiNoUser[member.id] = false
        }
        if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]==null){
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id] = 0.0
        }
    }
    @EventHandler
    internal suspend fun MemberLeaveEvent.handle0() {
        group.sendMessage(member.nick + " 离开了我们")
        for(groupTmp in bot.groups){
            if(groupTmp.members.contains(member.id)){
               return
            }
        }
        HoshinoYumemiKouKann.HoshinoYumemiNoKouKann.remove(member.id)
        HoshinoYumemiUser.HoshinoYumemiNoUser.remove(member.id)
        HoshinoYumemiMoney.HoshinoYumemiNoMoney.remove(member.id)
    }
    @EventHandler
    internal suspend fun GroupMessageEvent.handle0() {
        if (message.contentToString() == "签到" || (message.contentToString()
                .contains("签到") && message.contentToString().contains(At(bot).contentToString()))
        ) {
            if (!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch) {
                return
            }
            var kouKanAddValue = 0.0
            var moneyAddValue = 0.0
            if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] == null) {
                HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = 0.0
            }
            if (HoshinoYumemiUser.HoshinoYumemiNoUser[sender.id] == null) {
                HoshinoYumemiUser.HoshinoYumemiNoUser[sender.id] = false
            }
            if (HoshinoYumemiMoney.HoshinoYumemiNoMoney[sender.id] == null) {
                HoshinoYumemiMoney.HoshinoYumemiNoMoney[sender.id] = 0.0
            }
            if (HoshinoYumemiUser.HoshinoYumemiNoUser[sender.id] == true) {
                val msgChainTmp = buildMessageChain {
                    +At(sender)
                    +File("Yumemi/" + sender.id.toString() + ".png").uploadAsImage(group)
                    +PlainText("\n你今日已经签到过，这是你的本日数据")
                }
                group.sendMessage(msgChainTmp)
                return
            }
            launch {
                if (HoshinoYumemiUser.HoshinoYumemiNoUser[sender.id] == false) {
                    HoshinoYumemiUser.HoshinoYumemiNoUser[sender.id] = true
                    val rate = (0..10000).random()
                    if (rate > 9990) {
                        moneyAddValue = ((1500000..2000000).random().toDouble() / 100000.0)
                        kouKanAddValue = (30000..50000).random().toDouble() / 10000.0
                    } else if (rate > 9900) {
                        moneyAddValue = ((1000000..1500000).random().toDouble() / 100000.0)
                        kouKanAddValue = (15000..30000).random().toDouble() / 10000.0
                    } else if (rate > 9000) {
                        moneyAddValue = ((500000..1000000).random().toDouble() / 100000.0)
                        kouKanAddValue = (10000..15000).random().toDouble() / 10000.0
                    } else if (rate > 6000) {
                        moneyAddValue = ((100000..500000).random().toDouble() / 100000.0)
                        kouKanAddValue = (6000..10000).random().toDouble() / 10000.0
                    } else {
                        moneyAddValue = ((100..100000).random().toDouble() / 100000.0)
                        kouKanAddValue = (1..6000).random().toDouble() / 10000.0
                    }
                    if (HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(sender.id)) {
                        moneyAddValue = 0.0
                        kouKanAddValue *= -1.0
                    }
                    HoshinoYumemiMoney.HoshinoYumemiNoMoney[sender.id] =
                        HoshinoYumemiMoney.HoshinoYumemiNoMoney[sender.id]!! + moneyAddValue
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] =
                        kouKanAddValue + HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!!
                    if (HoshinoYumemiUser.HoshinoYumemiNoSpecialUser.contains(sender.id)) {
                        if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! > 1000.0) {
                            HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = 1000.0
                        }
                    } else if (sender.isOwner() || sender.isAdministrator()) {
                        if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! > 900.0) {
                            HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = 900.0
                        }
                    } else {
                        if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! > 800.0) {
                            HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = 800.0
                        }
                    }
                    if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! < -800.0) {
                        HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = -800.0
                    }
                }
                val msgChainTmp = buildMessageChain {
                    +At(sender)
                    +buildSignImage(sender, kouKanAddValue, moneyAddValue).uploadAsImage(group)
                }
                group.sendMessage(msgChainTmp)
            }
        }
    }
    @EventHandler
    internal suspend fun GroupMessageEvent.handle1() {
        if(message.contains(At(bot))){
            if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
                return
            }
            if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(sender.id)){
                group.sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName+"非常讨厌你")
                return
            }
            if(message.contentToString().contains("签到")){
                return
            }
            if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! <0){
                group.sendMessage("哼，不理你了")
                return
            }
            if(message.contentToString().replace(At(bot).contentToString(),"").replace(" ","")=="好感"||message.contentToString().contains("查看好感")||message.contentToString().contains("我的好感")){
                val msgChain = buildMessageChain {
                    +At(sender)
                    +PlainText("您当前的好感度是："+HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString())
                }
                group.sendMessage(msgChain)
                return
            }
            if(message.contentToString().replace(At(bot).contentToString(),"").replace(" ","")=="金币"||message.contentToString().contains("查看金币")){
                val msgChain = buildMessageChain {
                    +At(sender)
                    +PlainText("您当前的金币数是："+HoshinoYumemiMoney.HoshinoYumemiNoMoney[sender.id]!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString())
                }
                group.sendMessage(msgChain)
                return
            }
            if(message.contentToString().replace(At(bot).contentToString(),"").replace(" ","")=="打工"){
                userS[sender.id]?.work()?.let { group.sendMessage(it) }
                return
            }
            if(userS[sender.id]!!.testState){
                if(message.contentToString().replace(At(bot).contentToString()+"  ","").replace(At(bot).contentToString()+" ","").replace(At(bot).contentToString(),"")==HoshinoYumemiCourse.HoshinoYumemiNoCourses[userS[sender.id]!!.specialized]!!.find{it.keys.contains(userS[sender.id]!!.testQuest)}!![userS[sender.id]!!.testQuest]){
                    subject.sendMessage("恭喜你，答对了")
                    if(userS[sender.id]!!.degree<20){
                        userS[sender.id]!!.degree++
                    }
                }else{
                    subject.sendMessage(message.contentToString().replace(At(bot).contentToString(),"")+"不是正确的答案或格式错误，很遗憾，答错了")
                }
                userS[sender.id]!!.testState=false
                userS[sender.id]!!.testQuest=""
                return
            }
            for(key in HoshinoYumemiReplyList.HoshinoYumemiNoReplyList.keys){
                if(message.contentToString().contains(key)){
                    val kouKannValueThis = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!!.div(100.0).toLong()
                    if(HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[key]!!.find{kouKannValueThis in it.keys}==null){
                        return
                    }
                    val tmpSetOfMap = mutableSetOf<Map<Long,String>>()
                    for (map in HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[key]!!){
                        if(map.keys.contains(kouKannValueThis))
                        tmpSetOfMap.add(map)
                    }
                    val msgTmpAmount = tmpSetOfMap.size
                    val rand = msgTmpAmount.let { Random().nextInt(it) }
                    val msgTmp = tmpSetOfMap.elementAt(rand)[kouKannValueThis]
                    val msgChain = buildMessageChain {
                        +At(sender)
                        +PlainText(" $msgTmp")
                    }
                    group.sendMessage(msgChain)
                    return
                }
            }
            try {
                val cred = Credential(HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretId, HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretKey)
                val httpProfile = HttpProfile()
                httpProfile.endpoint = "nlp.tencentcloudapi.com"
                val clientProfile = ClientProfile()
                clientProfile.httpProfile = httpProfile
                val client = NlpClient(cred, "ap-guangzhou", clientProfile)
                val req = ChatBotRequest()
                req.flag = 0L
                val msgInput = message.contentToString().replace(At(bot).contentToString(),"")
                req.query = msgInput
                val resp = client.ChatBot(req)
                if(resp.reply.contains("喜欢")){
                    val msgChain = buildMessageChain {
                        +At(sender)
                        +PlainText(" ...")
                    }
                    group.sendMessage(msgChain)
                    return
                }
                val msgChain = buildMessageChain {
                    +At(sender)
                    +PlainText(" "+resp.reply.replace("腾讯小龙女",HoshinoYumemiConfig.HoshinoYumemiNoName).replace("小龙女",HoshinoYumemiConfig.HoshinoYumemiNoName).replace("姑姑",HoshinoYumemiConfig.HoshinoYumemiNoName))
                }
                group.sendMessage(msgChain)
            } catch (e: TencentCloudSDKException) {
                group.sendMessage(e.toString())
            }
        }
    }
    @EventHandler
    internal suspend fun GroupMessageEvent.handle2() {
        if(message.contains(At(bot))){
            if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
                return
            }
            if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! <0){
                return
            }
            if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(sender.id)){
                return
            }
            var respTmp = false;
            val msgTemp = message.contentToString().replace(At(bot).contentToString(),"")
            if(msgTemp.contains("签到")){
                return
            }
            try {
                val cred = Credential(HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretId, HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretKey)
                val httpProfile = HttpProfile()
                httpProfile.endpoint = "nlp.tencentcloudapi.com"
                val clientProfile = ClientProfile()
                clientProfile.httpProfile = httpProfile
                val client = NlpClient(cred, "ap-guangzhou", clientProfile)
                val req = TextSimilarityRequest()
                val targetText1 = arrayOf("傻逼", "笨蛋")
                req.srcText = msgTemp;
                req.targetText = targetText1;
                val resp = client.TextSimilarity(req)
                if(resp.similarity[0].score>0.9||resp.similarity[1].score>0.85){
                    respTmp = true
                }
            } catch (e: TencentCloudSDKException) {
                println(e.toString())
            }
            if(msgTemp.contains("抱抱")||msgTemp.contains("贴贴")||msgTemp.contains("摸头")||msgTemp.contains("摸一摸头")||msgTemp.contains("老婆")||msgTemp.contains("达令")||msgTemp.contains("亲亲")||msgTemp.contains("亲一下")||msgTemp.contains("亲一口")||msgTemp.contains("喜欢你")){
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! <500.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! - (200..1000).random().toDouble()/10000.0
                }else if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! >700.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! + (200..2000).random().toDouble()/10000.0
                }
            }else if(msgTemp.contains("爬")||msgTemp.contains("笨蛋")||msgTemp.contains("傻逼")||respTmp){
                if(HoshinoYumemiUser.HoshinoYumemiNoSpecialUser.contains(sender.id)){
                    return
                }else{
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! - (200..1000).random().toDouble()/500.0
                }
            }else if(msgTemp.contains("你真强")||msgTemp.contains("你真棒")||msgTemp.contains("你真厉害")||msgTemp.contains("谢谢")){
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!!<0){
                    return
                }
                HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! + (200..1000).random().toDouble()/10000.0
            }else{
                HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]!! + (200..1000).random().toDouble()/20000.0
            }
            if(HoshinoYumemiUser.HoshinoYumemiNoSpecialUser.contains(sender.id)){
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] !!> 1000.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]=1000.0
                }
            } else if(sender.isOwner()||sender.isAdministrator()){
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] !!> 900.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]=900.0
                }
            }else{
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] !!> 800.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]=800.0
                }
            }
            if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id] !!< -800.0){
                HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[sender.id]= -800.0
            }
        }
    }
    @EventHandler
    internal suspend fun GroupMessageEvent.handle3() {
        if(message.contains(At(bot))) {
            if (HoshinoYumemiUser.HoshinoYumemiNoSpecialUser.contains(sender.id)||sender.isAdministrator()||sender.isOwner()) {
                if(message.contentToString().contains("睡吧")){
                    if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
                        group.sendMessage("ZZZZZZZZZZZZ")
                    }
                    group.sendMessage("那么"+HoshinoYumemiConfig.HoshinoYumemiNoName+"就要休息了")
                    HoshinoYumemiSwitch.HoshinoYumemiNoSwitch = false
                }else if(message.contentToString().contains("起床了")){
                    if(HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
                        group.sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName+"没睡啊")
                    }
                    group.sendMessage("呜呜呜，还想多睡一会")
                    HoshinoYumemiSwitch.HoshinoYumemiNoSwitch = true
                }
            }
        }
    }
}