package srh.shirakana.hoshinoyumemi.command

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
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import okhttp3.OkHttpClient
import okhttp3.Request
import srh.shirakana.hoshinoyumemi.HoshiniYumemi
import srh.shirakana.hoshinoyumemi.file.*
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import java.math.RoundingMode
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

@Serializable
data class LoliconJson(val error: String, val data: List<Data>) {
    @Serializable
    data class Data(
        val pid: Int,
        val p: Int,
        val uid: Int,
        val title: String,
        val author: String,
        val r18: Boolean,
        val width: Int,
        val height: Int,
        val tags: List<String>,
        val ext: String,
        val uploadDate: Long,
        val urls: Urls
    ) {
        @Serializable
        data class Urls(val regular: String)
    }
}

@Serializable
data class SaucenaoJson(val header : Header,val result : List<Results>){
    @Serializable
    data class Header(
        val user_id: String,
        val account_type: String,
        val short_limit: String,
        val long_limit: String,
        val long_remaining: Int,
        val short_remaining: Int,
        val status: Int,
        val results_requested: Int,
        val index : Map<Int, INDEX>,
        val search_depth: String,
        val minimum_similarity: Double,
        val query_image_display: String,
        val query_image: String,
        val results_returned: Int
    ){
        @Serializable
        data class INDEX(
            val status :Int,
            val parent_id :Int,
            val id :Int,
            val results :Int
        )
    }
    @Serializable
    data class Results(
        val header: HEADER,
        val data: DATA
    ){
        @Serializable
        data class HEADER(
            val similarity :String,
            val thumbnail :String,
            val index_id :Int,
            val index_name :String,
            val dupes :Int,
            val hidden :Int
        )
        @Serializable
        data class DATA(
            val ext_urls:List<String>? = null,
            val path: String? = null,
            val creator:List<String>? = null,
            val creator_name:String? = null,
            val author_name:String? = null,
            val title:String? = null,
            val da_id:String? = null,
            val source:String? = null,
            val pixiv_id:Long? = null,
            val member_name:String? = null,
            val member_id:Long? = null,
            val anidb_aid:Long? = null,
            val mal_id:Long? = null,
            val anilist_id:Long? = null,
            val part:String? = null,
            val year:String? = null,
            val est_time:String? = null,
            val fa_id:Long? = null,
            val created_at:String? = null,
            val tweet_id:String? = null,
            val twitter_user_id:String? = null,
            val twitter_user_handle:String? = null,
            val eng_name:String? = null,
            val jp_name:String? = null,
            val bcy_type:String? = null,
            val bcy_id:Long? = null,
            val member_link_id:Long? = null,
            val fn_type:String? = null,
            val pawoo_id:Long? = null,
            val pawoo_user_acct:String? = null,
            val pawoo_user_username:String? = null,
            val type:String? = null,
            val mu_id:Long? = null,
            val pawoo_user_display_name:String? = null,
            val published:String? = null,
            val service:String? = null,
            val service_name:String? = null,
            val id:String? = null,
            val user_id:String? = null,
            val user_name:String? = null,
            val md_id:String? = null,
            val artist:String? = null,
            val author:String? = null,
            val author_url:String? = null
        ){
            @OptIn(ExperimentalSerializationApi::class)
            public fun toJsonString():String{
                return Json.encodeToString(this).replace("{","").replace("}","")
                    .replace(",\"","\n\"")
            }
        }
    }
}

object HoshinoYumemiKouKannCommand : CompositeCommand(
    HoshiniYumemi, "KouKann",
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
            graphics.drawString("[誓约/爱]",572,470+fixedOffsetY)
        }else if(kouKanLevel==9L){
            graphics.drawString("[至交]",572,470+fixedOffsetY)
        }else if(kouKanLevel>7){
            graphics.drawString("[朋友]",572,470+fixedOffsetY)
        }else if(kouKanLevel>5){
            graphics.drawString("[熟悉]",572,470+fixedOffsetY)
        }else if(kouKanLevel>3){
            graphics.drawString("[一般]",572,470+fixedOffsetY)
        }else if(kouKanLevel>1){
            graphics.drawString("[比较冷漠]",572,470+fixedOffsetY)
        }else if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[target.id]!! >-1){
            graphics.drawString("[冷漠]",572,470+fixedOffsetY)
        }else{
            graphics.drawString("[讨厌]",572,470+fixedOffsetY)
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

    @SubCommand
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
    @SubCommand
    @Description("减少好感度")
    suspend fun CommandSender.decrease(member : Member, Amount : Long) {
        if(HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!=null){
            HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id] = HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id]!! - Amount
            sendMessage(member.nick + " 的当前好感："+HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[member.id].toString())
        }else{
            sendMessage("用户不存在")
        }
    }
    @SubCommand
    @Description("添加黑名单")
    suspend fun CommandSender.bladd(Target : Long) {
        HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.add(Target)
        HoshinoYumemiKouKann.HoshinoYumemiNoKouKann[Target] = -800.0
    }
    @SubCommand
    @Description("移出黑名单")
    suspend fun CommandSender.blrem(Target : Long) {
        HoshinoYumemiBlackList.HoshinoYumemiNoBlackList.remove(Target)
    }
    @SubCommand
    @Description("增加好感度")
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
    @SubCommand
    @Description("减少好感度")
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
    HoshiniYumemi, "Money",
    description = "操作金钱",
){
    @SubCommand
    @Description("增加金钱")
    suspend fun CommandSender.add(member : Member, Amount : Long) {
        if(HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]!=null){
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id] = Amount + HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id]!!
            sendMessage(member.nick + " 的当前金钱："+HoshinoYumemiMoney.HoshinoYumemiNoMoney[member.id].toString())
        }else{
            sendMessage("用户不存在")
        }
    }
    @SubCommand
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
    HoshiniYumemi, "DataList",
    description = "操作回复列表与商店列表",
){
    @SubCommand
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
    @SubCommand
    @Description("删除指定回复")
    suspend fun CommandSender.replydel(input : String, KouKannLevel : Long, output : String) {
        if(HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]==null|| HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]?.contains(mapOf(KouKannLevel to output)) == false){
            return
        }
        HoshinoYumemiReplyList.HoshinoYumemiNoReplyList[input]?.remove(mapOf(KouKannLevel to output))
        sendMessage("删除成功")
    }
    @SubCommand
    @Description("添加商品")
    suspend fun CommandSender.shopadd(name : String, cost : Double) {
        HoshinoYumemiShop.HoshinoYumemiNoShop.add(mutableMapOf(name to cost))
        sendMessage("商品添加成功")
    }
    @SubCommand
    @Description("删除商品")
    suspend fun CommandSender.shopdel(name : String, cost : Double) {
        HoshinoYumemiShop.HoshinoYumemiNoShop.remove(mutableMapOf(name to cost))
        sendMessage("商品删除成功")
    }
}

object HoshinoYumemiTencentCloudAPI : CompositeCommand(
    HoshiniYumemi, "TCAPI",
    description = "腾讯云API",
){
    @SubCommand
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
            req.sourceText = input
            req.source = "auto"
            req.target = lang
            req.projectId = 0L
            val resp = client.TextTranslate(req)
            sendMessage("翻译结果：" + resp.targetText)
        }catch (e: TencentCloudSDKException) {
            sendMessage(e.toString())
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
    HoshiniYumemi, "eroImage",
    description = "涩图",
){

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun handleLoliconApi(r18 : Boolean = false, num : Int = 1, keyword : String = "", size : String = "regular", proxy : String = "i.pixiv.re", contact:Contact, user :User) : List<ForwardMessage.Node>{
        var urlStr = "https://api.lolicon.app/setu/v2?size=${size}&proxy=${proxy}&keyword=${keyword}&num=${num}"
        if(keyword == "rand"){
            urlStr = "https://api.lolicon.app/setu/v2?size=${size}&proxy=${proxy}&num=${num}"
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
    @SubCommand
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
                val resq: SaucenaoJson = jsonTmp.decodeFromString(data.body!!.string().replace("\"results\":[{","\"result\":[{").replace("\\/","/"));
                //.decodeFromString(test.replace("\"results\":[{","\"result\":[{"))
                if(resq.result.isNotEmpty()){
                    val msgChainHandle = mutableListOf<ForwardMessage.Node>()
                    for((i, element) in resq.result.withIndex()){
                        msgChainHandle.add(ForwardMessage.Node(user!!.id, i,"搜索者", buildMessageChain {
                            +PlainText("相似度：${element.header.similarity}\n${element.data.toJsonString()}")
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
    @SubCommand
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

object HoshinoYumemiUserCommand : CompositeCommand(
    HoshiniYumemi, "usrcmd",
    description = "用户指令",
){
    @SubCommand
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
    @SubCommand
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
    @SubCommand
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
    @SubCommand
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
}