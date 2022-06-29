package srh.shirakana.hoshinoyumemi.command

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature
import com.tencentcloudapi.common.Credential
import com.tencentcloudapi.common.exception.TencentCloudSDKException
import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.HttpProfile
import com.tencentcloudapi.tmt.v20180321.TmtClient
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateRequest
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import okhttp3.OkHttpClient
import okhttp3.Request
import srh.shirakana.hoshinoyumemi.BotJobs
import srh.shirakana.hoshinoyumemi.HoshiniYumemi
import srh.shirakana.hoshinoyumemi.file.*
import srh.shirakana.hoshinoyumemi.userJobs
import srh.shirakana.hoshinoyumemi.userS
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import java.math.RoundingMode
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

object HoshinoYumemiKouKannCommand : CompositeCommand(
    HoshiniYumemi, "KouKann","好感度",
    description = "操作好感度",
){

    @JvmStatic
    val KouKanBar: BufferedImage = ImageIO.read(HoshinoYumemiKouKannCommand::class.java.classLoader.getResource("bar.png"))
    @JvmStatic
    val KouKanBarEmpty: BufferedImage = ImageIO.read(HoshinoYumemiKouKannCommand::class.java.classLoader.getResource("bar_white.png"))
    @JvmStatic
    val BgImage1: BufferedImage = ImageIO.read(HoshinoYumemiKouKannCommand::class.java.classLoader.getResource("Bg1.png"))
    @JvmStatic
    val BgImage2: BufferedImage = ImageIO.read(HoshinoYumemiKouKannCommand::class.java.classLoader.getResource("Bg2.png"))

    fun buildSignImage (target : Member,kouKanAdd : Double,moneyAdd : Double) : ExternalResource {
        val newImg = BufferedImage(1280, 610, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = newImg.createGraphics()
        graphics.color = Color(0,0,0)
        val rate = (1..2).random()
        val kouKanLevel = (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[target.id]!! / 100.0).toLong()
        val kouKanValue = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[target.id]!!.toLong()%100L
        graphics.drawImage(ImageIO.read(URL(target.avatarUrl)), 87, 314,184,184, null)
        graphics.drawImage(KouKanBarEmpty, 422, 390,320,32, null)
        graphics.drawImage(KouKanBar, 422, 390,(3.2*kouKanValue).toInt(),32, null)
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[target.id]!!>99||kouKanValue == 0L){
            graphics.drawImage(KouKanBar, 422, 390,320,32, null)
        }
        if(rate == 1){
            graphics.drawImage(BgImage1, 0, 0, null)
        }else{
            graphics.drawImage(BgImage2, 0, 0, null)
        }
        graphics.font = Font("楷体", Font.PLAIN ,18)
        val fixedOffsetY = graphics.fontMetrics.ascent - (graphics.fontMetrics.height / 2 - 18 / 2)
        graphics.drawString(kouKanLevel.toString(),536,438+fixedOffsetY)
        if(kouKanLevel==10L){
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[誓约/爱]",438,470+fixedOffsetY)
        }else if(kouKanLevel==9L){
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[至交]",438,470+fixedOffsetY)
        }else if(kouKanLevel>7){
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[朋友]",438,470+fixedOffsetY)
        }else if(kouKanLevel>5){
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[熟悉]",438,470+fixedOffsetY)
        }else if(kouKanLevel>3){
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[一般]",438,470+fixedOffsetY)
        }else if(kouKanLevel>1){
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[比较冷漠]",438,470+fixedOffsetY)
        }else if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[target.id]!! >-1){
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[冷漠]",438,470+fixedOffsetY)
        }else{
            graphics.drawString("${HoshinoYumemiConfig.HoshinoYumemiNoName}对你的态度：[讨厌]",438,470+fixedOffsetY)
        }
        graphics.drawString((100-kouKanValue).toString(),552,500+fixedOffsetY)
        graphics.font = Font("楷体", Font.PLAIN ,24)
        val fixedY = graphics.fontMetrics.ascent - (graphics.fontMetrics.height / 2 - 24 / 2)
        graphics.drawString("+"+kouKanAdd.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString(),1010,315+fixedY)
        graphics.drawString("+"+moneyAdd.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString(),983,345+fixedY)
        graphics.drawString("时间："+Date().toString(),400,540+fixedY)
        graphics.font = Font("楷体", Font.PLAIN ,36)
        val yFixed = graphics.fontMetrics.ascent - (graphics.fontMetrics.height / 2 - 36 / 2)
        graphics.drawString(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[target.id]!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString(),620,340+yFixed)
        graphics.dispose()
        val uuidFileName="Yumemi/"+target.id.toString()+".png"
        val outPutImage = File(uuidFileName)
        ImageIO.write(newImg, "png", outPutImage)
        return outPutImage.toExternalResource()
    }

    @OptIn(ConsoleExperimentalApi::class)
    @SubCommand("add","添加")
    @Description("增加好感度")
    suspend fun CommandSender.add(member : Member, Amount : Long) {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!=null){
            HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = Amount + HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!!
            if(HoshinoYumemiUser.HoshinoYumemiNoSpecialUser.contains(member.id)){
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! >1000.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 1000.0
                }
            }else if(member.isOwner()||member.isAdministrator()){
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! >900.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 900.0
                }
            }else{
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! >800.0){
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 800.0
                }
            }
            sendMessage(member.nick + " 的当前好感："+HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id].toString())
        }else{
            sendMessage("用户不存在")
        }
    }
    @SubCommand("decrease","减少")
    @Description("减少好感度")
    suspend fun CommandSender.decrease(member : Member, Amount : Long) {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!=null){
            HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! - Amount
            sendMessage(member.nick + " 的当前好感："+HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id].toString())
        }else{
            sendMessage("用户不存在")
        }
    }
    @SubCommand("bladd","加入黑名单")
    @Description("添加黑名单")
    suspend fun CommandSender.bladd(Target : Long) {
        HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.add(Target)
        HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[Target] = -800.0
    }
    //
    @SubCommand("blrem","移除黑名单")
    @Description("移出黑名单")
    suspend fun CommandSender.blrem(Target : Long) {
        HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.remove(Target)
    }
    @SubCommand("addall","全体添加")
    @Description("给全体增加好感度")
    suspend fun CommandSender.addall(Amount : Long) {
        val memberSet = subject?.id?.let { bot?.getGroup(it)?.members } ?: return
        for(member in memberSet) {
            if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] != null) {
                HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] =
                    Amount + HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!!
                if (HoshinoYumemiUser.HoshinoYumemiNoSpecialUser.contains(member.id)) {
                    if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! > 1000.0) {
                        HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 1000.0
                    }
                } else if (member.isOwner() || member.isAdministrator()) {
                    if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! > 900.0) {
                        HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 900.0
                    }
                } else {
                    if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! > 800.0) {
                        HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = 800.0
                    }
                }
            }
        }
        sendMessage("完成")
    }
    @SubCommand("decreaseall","全体减少")
    @Description("给全体减少好感度")
    suspend fun CommandSender.decreaseall(Amount : Long) {
        val memberSet = subject?.id?.let { bot?.getGroup(it)?.members } ?: return
        for(member in memberSet) {
            if (HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] != null) {
                HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] =
                    HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! - Amount
                sendMessage(member.nick + " 的当前好感：" + HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id].toString())
            }
        }
        sendMessage("完成")
    }
}

object HoshinoYumemiMoneyCommand : CompositeCommand(
    HoshiniYumemi, "Money","金钱",
    description = "操作金钱",
){
    @SubCommand("add","添加")
    @Description("增加金钱")
    suspend fun CommandSender.add(member : Member, Amount : Long) {
        if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]!=null){
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id] = Amount + HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]!!
            sendMessage(member.nick + " 的当前金钱："+HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id].toString())
        }else{
            sendMessage("用户不存在")
        }
    }
    @SubCommand("decrease","减少")
    @Description("减少金钱")
    suspend fun CommandSender.decrease(member : Member, Amount : Long) {
        if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]!=null){
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id] = HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]!! - Amount
            sendMessage(member.nick + " 的当前金钱："+HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id].toString())
        }else{
            sendMessage("用户不存在")
        }
    }
}

object HoshinoYumemiReplyListCommand : CompositeCommand(
    HoshiniYumemi, "DataList","数据",
    description = "操作回复列表与商店列表",
){
    @SubCommand("replyadd","添加回复")
    @Description("增加回复")
    suspend fun CommandSender.replyadd(input : String, KouKannLevel : Long, output : String) {
        if(KouKannLevel>10){
            sendMessage("KouKannLevel最大为10")
            return
        }else{
            if(HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]==null){
                HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input] = mutableSetOf()
            }
            HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]?.add(mapOf(KouKannLevel to output))
            sendMessage("添加成功")
        }
    }
    @SubCommand("replydel","删除回复")
    @Description("删除指定回复")
    suspend fun CommandSender.replydel(input : String, KouKannLevel : Long, output : String) {
        if(HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]==null|| HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]?.contains(mapOf(KouKannLevel to output)) == false){
            return
        }
        HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]?.remove(mapOf(KouKannLevel to output))
        if(HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]?.isEmpty() == true){
            HoshinoYumemiReplyList.HoshinoYumemiNoReplyList.remove(input)
        }
        sendMessage("删除成功")
    }
    @SubCommand("shopadd","添加商品")
    @Description("添加商品")
    suspend fun CommandSender.shopadd(name : String, cost : Double) {
        HoshinoYumemiShop.HoshinoYumemiNoShop.add(mutableMapOf(name to cost))
        sendMessage("商品添加成功")
    }
    @SubCommand("shopdel","删除商品")
    @Description("删除商品")
    suspend fun CommandSender.shopdel(name : String, cost : Double) {
        HoshinoYumemiShop.HoshinoYumemiNoShop.remove(mutableMapOf(name to cost))
        sendMessage("商品删除成功")
    }
}

object HoshinoYumemiTencentCloudAPI : CompositeCommand(
    HoshiniYumemi, "TCAPI","腾讯云",
    description = "腾讯云API",
){
    @SubCommand("机器翻译","mt")
    @Description("机器翻译(input：输入的语句，lang：目标语言：zh（简体中文）：en（英语）、ja（日语）、ko（韩语）、fr（法语）、es（西班牙语）、it（意大利语）、de（德语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）、vi（越南语）、id（印尼语）、th（泰语）、ms（马来语）)")
    suspend fun CommandSender.MT(input : String ,lang : String) {
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName+"绝对不会为你做事的")
            return
        }
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <400.0){
            sendMessage("我为什么要为你做这些？")
            return
        }else if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <700.0){
            if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[user?.id]!! < input.length*3 ){
                sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName + "酱是需要报酬才可以帮你做事的呢，但是你的金币貌似不够("+(input.length*3).toString()+")")
                return
            }
            sendMessage("扣除了"+(input.length*3).toString()+"金币")
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id] = HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!! - (input.length*3).toDouble()
        }
        try {
            val cred = Credential(
                HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretId,
                HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretKey
            )
            val httpProfile = HttpProfile()
            httpProfile.endpoint = "tmt.tencentcloudapi.com"
            val clientProfile = ClientProfile()
            clientProfile.httpProfile = httpProfile
            val client = TmtClient(cred, "ap-beijing", clientProfile)
            val req = TextTranslateRequest()
            req.sourceText = input.replace("_"," ")
            req.source = "auto"
            req.target = lang
            req.projectId = 0L
            val resp = client.TextTranslate(req)
            sendMessage("翻译结果：" + resp.targetText)
        }catch (e: TencentCloudSDKException) {
            if(e.toString().contains("不支持的语种")){
                try{
                    val cred = Credential(
                        HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretId,
                        HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretKey
                    )
                    val httpProfile = HttpProfile()
                    httpProfile.endpoint = "tmt.tencentcloudapi.com"
                    val clientProfile = ClientProfile()
                    clientProfile.httpProfile = httpProfile
                    val client = TmtClient(cred, "ap-beijing", clientProfile)
                    val requestTmp = TextTranslateRequest()
                    requestTmp.sourceText = input.replace("_"," ")
                    requestTmp.source = "auto"
                    requestTmp.target = "zh"
                    requestTmp.projectId = 0L
                    val respTmp = client.TextTranslate(requestTmp)
                    requestTmp.sourceText = respTmp.targetText
                    requestTmp.source = "zh"
                    requestTmp.target = lang
                    val respOut = client.TextTranslate(requestTmp)
                    sendMessage("翻译结果：" + respOut.targetText)
                }catch (f: TencentCloudSDKException) {
                    sendMessage(f.toString())
                }
            }else{
                sendMessage(e.toString())
            }
        }
    }
    /*@SubCommand
    @Description("OCR")
    suspend fun CommandSender.OCR(input : Image) {
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName+"绝对不会为你做事的")
            return
        }
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <500.0){
            sendMessage("我为什么要为你做这些？")
            return
        }else if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <800.0){
            if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[user?.id]!! <1000.0){
                sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName + "酱是需要报酬才可以帮你做事的呢，但是你的金币貌似不够（1000）")
                return
            }
            sendMessage("扣除了1000金币")
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id] = HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!! - 1000.0
        }
        try {
            val cred = Credential(
                HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretId,
                HoshinoYumemiTencentCloudApiConfig.HoshinoYumemiSecretKey
            )
            val httpProfile = HttpProfile()
            httpProfile.endpoint = "ocr.tencentcloudapi.com"
            val clientProfile = ClientProfile()
            clientProfile.httpProfile = httpProfile
            val client = OcrClient(cred, "", clientProfile)
            val req = GeneralHandwritingOCRRequest()
            req.imageUrl=input.queryUrl()
            val resp = client.GeneralHandwritingOCR(req)
            sendMessage("结果："+resp.textDetections)
        } catch (e: TencentCloudSDKException) {
            sendMessage(e.toString())
        }
    }
     */
}

object HoshinoYumemiSeTuCommand : CompositeCommand(
    HoshiniYumemi, "eroImage","涩图","色图",
    description = "涩图",
){

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun handleLoliconApi(r18 : Boolean = false, num : Int = 1, keyword : String = "", size : String = "regular", proxy : String = "i.pixiv.re", contact:Contact, user :User) : List<ForwardMessage.Node>{
        var urlStr = "https://api.lolicon.app/setu/v2?size=${size}&proxy=${proxy}&keyword=${keyword}&num=${num}"
        if(keyword == "rand"){
            urlStr = "https://api.lolicon.app/setu/v2?size=${size}&proxy=${proxy}&num=${num}"
        }
        urlStr.replace("r18=1","").replace("r18=2","")
        if(urlStr.contains("r18")||urlStr.contains("R18")){
            contact.sendMessage("不可以瑟瑟")
            return mutableListOf<ForwardMessage.Node>()
        }
        var req = OkHttpClient()
        val data = req.newCall(Request.Builder().url(urlStr).build()).execute()
        val resq : LoliconJson = Json.decodeFromString(data.body!!.string())
        if(resq.data.isEmpty()){
            return mutableListOf<ForwardMessage.Node>()
        }else{
            val msgChainHandle = mutableListOf<ForwardMessage.Node>()
            for((i, element) in resq.data.withIndex()){
                val outPutImage = withContext(Dispatchers.IO) {
                    ImageIO.read(URL(element.urls.regular))
                }
                val newImage = File("Yumemi/${UUID.randomUUID().toString().replace("-","")}.jpg")
                withContext(Dispatchers.IO) {
                    ImageIO.write(outPutImage, "png", newImage)
                }
                msgChainHandle.add(ForwardMessage.Node(user.id, i,"LSP", buildMessageChain {
                    +PlainText("Pixiv ID:${element.pid}\n作者:${element.author}\n标签:${element.tags}\n")
                    +newImage.uploadAsImage(contact)
                }))
            }
            return msgChainHandle
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @SubCommand("s","搜索")
    @Description("搜图")
    suspend fun CommandSender.s(image : Image) {
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName+"绝对不会为你做事的")
            return
        }
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <500.0){
            sendMessage("我为什么要为你做这些？")
            return
        }else if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <800.0){
            if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[user?.id]!! < 2000 ){
                sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName + "酱是需要报酬才可以帮你做事的呢，但是你的金币貌似不够(2000)")
                return
            }
            sendMessage("扣除了2000金币")
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id] = HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!! - 2000.0.toDouble()
        }
        try {
            val url = image.queryUrl()
            var req = OkHttpClient()
            val data = req.newCall(
                Request.Builder().url("https://saucenao.com/search.php?db=999&output_type=2&testmode=1&numres=6&url=${url}&api_key=${HoshinoYumemiSaucenaoApiConfig.HoshinoYumemiSaucenaoSecretKey}").get().build()
            ).execute()
            if(data.isSuccessful) {
                //sendMessage("https://saucenao.com/search.php?db=999&output_type=2&testmode=1&numres=6&url=${url}&api_key=${HoshinoYumemiSaucenaoApiConfig.HoshinoYumemiSaucenaoSecretKey}")
                val jsonTmp = Json{
                    ignoreUnknownKeys = true
                    encodeDefaults =true
                    coerceInputValues=true
                }
                val resq: SaucenaoJson = jsonTmp.decodeFromString(data.body!!.string());
                //.decodeFromString(test.replace("\"results\":[{","\"result\":[{"))
                if(resq.results.isNotEmpty()){
                    val msgChainHandle = mutableListOf<ForwardMessage.Node>()
                    for((i, element) in resq.results.withIndex()){
                        msgChainHandle.add(ForwardMessage.Node(user!!.id, i,"搜索者", buildMessageChain {
                            +PlainText("相似度："+element.header.similarity+"%\n")
                            +PlainText(element.data.toJsonString().replace("\\/","/"))
                        }))
                    }
                    val msg = RawForwardMessage(msgChainHandle).render(
                        object : ForwardMessage.DisplayStrategy {
                            override fun generateTitle(forward: RawForwardMessage) = "搜索结果"
                            override fun generateSummary(forward: RawForwardMessage) = "搜索结果"
                        }
                    )
                    sendMessage(msg)?.recallIn(2*60*1000)
                }
            }else{
                if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <800.0){
                    sendMessage("返还了2000金币")
                    HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id] = HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!! + 2000.0.toDouble()
                }
            }
        }catch(e :Exception){
            println(e.toString())
        }
    }
    @OptIn(ExperimentalSerializationApi::class)
    @SubCommand("g","发几张")
    @Description("获取涩图")
    suspend fun CommandSender.g(tag : String,amount : Long) {
        if(subject==null||user==null){
            return
        }
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName+"绝对不会为你做事的")
            return
        }
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <400.0){
            sendMessage("我为什么要为你做这些？")
            return
        }else if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user?.id]!! <700.0){
            if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[user?.id]!! < 100 *amount){
                sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName + "酱是需要报酬才可以帮你做事的呢，但是你的金币貌似不够(${100*amount})")
                return
            }
            if(amount>5){
                sendMessage("少冲点")
                return
            }
            if(amount<1){
                sendMessage("参数错误")
                return
            }
            sendMessage("扣除了${100*amount}金币")
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id] = HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!! - (100.0*amount).toDouble()
        }
        if(amount>5){
            sendMessage("少冲点")
            return
        }
        if(amount<1){
            sendMessage("参数错误")
            return
        }
        coroutineScope {
            launch {
                try {
                    val msg = RawForwardMessage(handleLoliconApi(false, amount.toInt(), tag, "regular", "i.pixiv.re", subject!!, user!!)).render(
                        object : ForwardMessage.DisplayStrategy {
                            override fun generateTitle(forward: RawForwardMessage) = "${forward.nodeList.size}张${tag}色图"
                            override fun generateSummary(forward: RawForwardMessage) = "查看${forward.nodeList.size}张${tag}色图"
                        }
                    )
                    if(msg.nodeList.isEmpty()){
                        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!!<700) {
                        HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id] = HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!! + (100.0*amount).toDouble()
                            sendMessage("获取失败,返还了${100 * amount}金币")
                        }
                    }else{
                        sendMessage(msg)?.recallIn(30000)
                        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!!<700) {
                            HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id] =
                                HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!! + (100.0 * (amount - msg.nodeList.size.toLong())).toDouble()
                        }
                    }
                } catch (e: Exception) {
                    println(e.toString())
                }
            }
        }
    }
}

object HoshinoYumemiWorkCommand : CompositeCommand(
    HoshiniYumemi, "AdmCmd","上帝指令",
    description = "管理员指令",
){
    @SubCommand("Daixuexi","带学习")
    @Description("带学习提醒")
    suspend fun CommandSender.Daixuexi(Mode : String ,GroupId : Long) {
        if(bot==null){
            return
        }
        if(Mode=="add"){
            if(GroupId==0L){
                for(group in bot!!.groups){
                    HoshinoYumemiDaiXueXi.HoshinoYumemiNoDaiXueXi.add(group.id)
                    sendMessage("已在全部群中启用")
                }
            }else{
                if(bot!!.getGroup(GroupId)!=null){
                    if(HoshinoYumemiDaiXueXi.HoshinoYumemiNoDaiXueXi.add(GroupId)){
                        sendMessage("添加成功")
                    }else{
                        sendMessage("添加失败")
                    }
                }
            }
            return
        }
        if(Mode=="del"){
            if(GroupId==0L){
                HoshinoYumemiDaiXueXi.HoshinoYumemiNoDaiXueXi.clear()
                sendMessage("已在所有群中禁用")
            }else{
                if(HoshinoYumemiDaiXueXi.HoshinoYumemiNoDaiXueXi.remove(GroupId)){
                    sendMessage("删除成功")
                }else{
                    sendMessage("删除失败")
                }
            }
            return
        }
        sendMessage("参数错误")
    }
    @SubCommand("Disable","NLP")
    @Description("禁用NLP的群聊")
    suspend fun CommandSender.Disable(Mode : String ,GroupId : Long) {
        if(bot==null){
            return
        }
        if(Mode=="add"){
            if(GroupId==0L){
                for(group in bot!!.groups){
                    HoshinoYumemiTencentCloudDisabledGroups.HoshinoYumemiNoTencentCloudDisabledGroups.add(group.id)
                    sendMessage("已在全部群中禁用")
                }
            }else{
                if(bot!!.getGroup(GroupId)!=null){
                    if(HoshinoYumemiTencentCloudDisabledGroups.HoshinoYumemiNoTencentCloudDisabledGroups.add(GroupId)){
                        sendMessage("添加成功")
                    }else{
                        sendMessage("添加失败")
                    }
                }
            }
            return
        }
        if(Mode=="del"){
            if(GroupId==0L){
                HoshinoYumemiTencentCloudDisabledGroups.HoshinoYumemiNoTencentCloudDisabledGroups.clear()
                sendMessage("已在所有群中启用")
            }else{
                if(HoshinoYumemiTencentCloudDisabledGroups.HoshinoYumemiNoTencentCloudDisabledGroups.remove(GroupId)){
                    sendMessage("删除成功")
                }else{
                    sendMessage("删除失败")
                }
            }
            return
        }
        sendMessage("参数错误")
    }
    @OptIn(ExperimentalSerializationApi::class)
    @SubCommand("添加工作","addWork")
    @Description("添加工作")
    suspend fun CommandSender.addWork(requiredSpecialize : String,reward : Double) {
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[requiredSpecialize]==null){
            sendMessage("不存在当前专业")
            return
        }
        if(userJobs.job.find{it.requiredSpecialized==requiredSpecialize}!=null){
            userJobs.job.find{it.requiredSpecialized==requiredSpecialize}!!.reward=reward
            sendMessage("已存在该工作，修改了reward")
        }
        if(userJobs.job.add(BotJobs.JobsBot(requiredSpecialize, reward))){
            sendMessage("添加成功")
            val jobsDataFile = File("data/srh.shirakana.hoshinoyumemi.plugin/Jobs.json")
            val userJobsJson = Json.encodeToString(userJobs)
            val jsonObjectUserJobs: JSONObject = JSONObject.parseObject(userJobsJson)
            val formatStrUserJobs: String = JSON.toJSONString(
                jsonObjectUserJobs, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat
            )
            jobsDataFile.writeText(formatStrUserJobs)
        }else{
            sendMessage("添加失败")
        }
    }
    @OptIn(ExperimentalSerializationApi::class)
    @SubCommand("删除工作","delWork")
    @Description("删除工作")
    suspend fun CommandSender.delWork(requiredSpecialize : String) {
        if(userJobs.job.remove(userJobs.job.find{it.requiredSpecialized == requiredSpecialize})){
            sendMessage("删除成功")
            val jobsDataFile = File("data/srh.shirakana.hoshinoyumemi.plugin/Jobs.json")
            val userJobsJson = Json.encodeToString(userJobs)
            val jsonObjectUserJobs: JSONObject = JSONObject.parseObject(userJobsJson)
            val formatStrUserJobs: String = JSON.toJSONString(
                jsonObjectUserJobs, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat
            )
            jobsDataFile.writeText(formatStrUserJobs)
        }else{
            sendMessage("删除失败")
        }
    }
    @OptIn(ExperimentalSerializationApi::class)
    @SubCommand("添加考试题目","addTest")
    @Description("添加考试题目")
    suspend fun CommandSender.addTest(Specialize : String,Question : String,Answer : String) {
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]==null){
            HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]= mutableSetOf()
        }
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]!!.find{it.keys.contains(Question)}!=null){
            sendMessage("该题目已经存在")
            return
        }
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]!!.add(mapOf(Question to Answer))){
            sendMessage("添加成功")
        }else{
            sendMessage("添加失败")
        }
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]!!.isEmpty()){
            HoshinoYumemiCourse.HoshinoYumemiNoCourses.remove(Specialize)
        }
    }
    @OptIn(ExperimentalSerializationApi::class)
    @SubCommand("删除考试题目","delTest")
    @Description("删除考试题目")
    suspend fun CommandSender.delTest(Specialize : String,Question : String) {
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]==null){
            sendMessage("不存在此专业")
            return
        }
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]?.
            remove(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]?.find{it.keys.elementAt(0)==Question})
                ==true){
            sendMessage("删除成功")
        }else{
            sendMessage("删除失败")
        }
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[Specialize]!!.isEmpty()){
            HoshinoYumemiCourse.HoshinoYumemiNoCourses.remove(Specialize)
        }
    }
}

object HoshinoYumemiUserCommand : CompositeCommand(
    HoshiniYumemi, "usrcmd","我的",
    description = "用户指令",
){
    @SubCommand("查看好感","kk")
    @Description("查看好感")
    suspend fun CommandSender.kk() {
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(user==null)return;
        val msgChain = buildMessageChain {
            +At(user!!)
            +PlainText("您当前的好感度是："+HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString())
        }
        sendMessage(msgChain)
    }
    @SubCommand("查看金钱","mon")
    @Description("查看金钱")
    suspend fun CommandSender.mon() {
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(user==null)return;
        val msgChain = buildMessageChain {
            +At(user!!)
            +PlainText("您当前拥有的金币数量是："+HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString())
        }
        sendMessage(msgChain)
    }
    @SubCommand("查看礼物","giftlist")
    @Description("查看存在的礼物")
    suspend fun CommandSender.giftlist() {
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(HoshinoYumemiShop.HoshinoYumemiNoShop.isEmpty()){
            sendMessage("商店中无任何礼物")
            return
        }
        var msg = ""
        for((index, good) in HoshinoYumemiShop.HoshinoYumemiNoShop.withIndex()){
            msg+="[ID:$index]：\n"
            for((key,value) in good){
                msg+="名称：[$key] 价格：[$value]"
            }
            msg+="\n"
        }
        sendMessage(msg)
    }
    @SubCommand("送礼物","gift")
    @Description("给BOT送礼物")
    suspend fun CommandSender.gift(ID : Long) {
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(user==null)return;
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            sendMessage(HoshinoYumemiConfig.HoshinoYumemiNoName+"不接受你的礼物！")
            return
        }
        if(ID < HoshinoYumemiShop.HoshinoYumemiNoShop.size){
            val good = HoshinoYumemiShop.HoshinoYumemiNoShop.elementAt(ID.toInt())
            var cost=0.0
            var name=""
            for((key,value)in good){
                cost=value
                name=key
            }
            if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!!>cost){
                HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]= HoshinoYumemiMoney.HoshinoYumemiNoMoney[user!!.id]!!-cost
                val msgChain = buildMessageChain {
                    +PlainText("感谢")
                    +At(user!!)
                    +PlainText("送我$name")
                }
                HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!! + (cost/1000.0).toDouble()
                sendMessage(msgChain)
            }else{
                sendMessage("金币不足")
            }
        }else{
            sendMessage("不存在该ID")
        }
    }
    @SubCommand("参加考试","jointest")
    @Description("参加指定专业的考试")
    suspend fun CommandSender.jointest(name : String) {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!! <0){
            subject!!.sendMessage("哼，不理你了")
            return
        }
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(user==null)return;
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            return
        }
        if(userS[user!!.id]?.testState==true){
            sendMessage("请勿重复参加考试")
            return
        }
        if(!HoshinoYumemiCourse.HoshinoYumemiNoCourses.keys.contains(name)){
            sendMessage("没有指定的专业")
            return
        }
        if(userS[user!!.id]?.specialized!="null"&&userS[user!!.id]?.specialized!=name){
            sendMessage("若参加其他专业考试，不会增长您的等级")
        }
        userS[user!!.id]?.testSpecialize=name
        if(userS[user!!.id]?.specialized=="null"){
            userS[user!!.id]?.specialized=name
        }
        if(HoshinoYumemiCourse.HoshinoYumemiNoCourses[name]!!.isEmpty())return
        val testQuestIndex = HoshinoYumemiCourse.HoshinoYumemiNoCourses[name]!!.size-1
        userS[user!!.id]?.testQuest=
            HoshinoYumemiCourse.HoshinoYumemiNoCourses[name]!!.elementAt((0..testQuestIndex).random()).keys.elementAt(0)
        userS[user!!.id]?.testState=true
        sendMessage(buildMessageChain {
            +At(user!!)
            +PlainText("题库中共有${testQuestIndex+1}道题\n题目：\n${ userS[user!!.id]?.testQuest!!.replace("<_>"," ").replace("<tab>","\t").replace("<enter>","\n") }\n请回答（需要ATBOT并输入答案）")
        })
    }
    @SubCommand("退出考试","exittest")
    @Description("退出考试")
    suspend fun CommandSender.exittest() {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!! <0){
            subject!!.sendMessage("哼，不理你了")
            return
        }
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            return
        }
        if(user==null)return;
        if(userS[user!!.id]?.testState==true){
            userS[user!!.id]?.testState=false
            userS[user!!.id]?.testSpecialize=""
            userS[user!!.id]?.testQuest=""
            sendMessage("退出成功")
            return
        }
    }
    @SubCommand("有什么专业","LS")
    @Description("列出目前存在的专业")
    suspend fun CommandSender.LS() {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!! <0){
            subject!!.sendMessage("哼，不理你了")
            return
        }
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(user==null)return;
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            return
        }
        sendMessage("目前有的专业：${HoshinoYumemiCourse.HoshinoYumemiNoCourses.keys.toString()}")
    }
    @SubCommand("我的专业","mysp")
    @Description("显示自己的专业及学位")
    suspend fun CommandSender.mysp() {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!! <0){
            subject!!.sendMessage("哼，不理你了")
            return
        }
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(user==null)return;
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            return
        }
        sendMessage("目前有的专业：${userS[user!!.id]!!.specialized}\n目前的学位：${userS[user!!.id]!!.degree}")
    }
    @SubCommand("放弃学位","giveupmysp")
    @Description("放弃当前学位")
    suspend fun CommandSender.giveupmysp() {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[user!!.id]!! <0){
            subject!!.sendMessage("哼，不理你了")
            return
        }
        if(!HoshinoYumemiSwitch.HoshinoYumemiNoSwitch){
            return
        }
        if(user==null)return;
        if(HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.contains(user!!.id)){
            return
        }
        userS[user!!.id]!!.specialized="null"
        userS[user!!.id]!!.degree=0
        sendMessage("放弃成功")
    }
}
