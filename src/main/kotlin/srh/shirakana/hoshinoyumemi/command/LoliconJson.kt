package srh.shirakana.hoshinoyumemi.command

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

