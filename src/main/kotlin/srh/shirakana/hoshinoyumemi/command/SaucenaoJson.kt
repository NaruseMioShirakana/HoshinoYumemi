package srh.shirakana.hoshinoyumemi.command

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class SaucenaoJson(val header : Header,val results : List<Results>){
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