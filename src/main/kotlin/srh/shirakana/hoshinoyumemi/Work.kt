package srh.shirakana.hoshinoyumemi

import kotlinx.serialization.Serializable
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import srh.shirakana.hoshinoyumemi.file.HoshinoYumemiMoney
import kotlin.math.E
import kotlin.math.ln

@Serializable
data class UserWorks(val userdata : MutableList<UserWorksData> = mutableListOf<UserWorksData>()){

    @Serializable
    data class UserWorksData(
        val id: Long,
        var degree: Int,
        var specialized: String,
        var state: Boolean,
        var testQuest: String,
        var testState: Boolean,
        var testSpecialize:String
    ){
        public fun work():MessageChain{
            if(this.state){
                return buildMessageChain {
                    +At(id)
                    +PlainText("你今天已经工作过了")
                }
            }
            if(this.specialized=="null"){return buildMessageChain {
                +At(id)
                +PlainText("你不具备任何专业的学位")
            }}
            if(userJobs[specialized]==null){
                return buildMessageChain {
                    +At(id)
                    +PlainText("该专业无对应的工作")
                }
            }
            this.state = true
            val moneyAddValue = userJobs[specialized]!!.reward*ln(degree * E)
            HoshinoYumemiMoney.HoshinoYumemiNoMoney[this.id] =
                HoshinoYumemiMoney.HoshinoYumemiNoMoney[this.id]!! + moneyAddValue
            return buildMessageChain {
                +At(id)
                +PlainText("你工作了一天，赚取了${moneyAddValue}报酬")
            }
        }
    }
    public operator fun get(index :Long):UserWorksData?{
        return this.userdata.find{it.id==index}
    }
}

@Serializable
data class BotJobs(val job : MutableList<JobsBot> = mutableListOf<JobsBot>()){

    @Serializable
    data class JobsBot(
        val requiredSpecialized :String = "",
        var reward: Double = 0.0
    ){
    }
    public operator fun get(index :String):JobsBot?{
        return this.job.find{it.requiredSpecialized==index}
    }
}