package de.zitzmanncedric.discordbot.audio.lyrics

import de.zitzmanncedric.discordbot.config.MainConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object GeniusAPI {
    private const val baseURL: String = "https://genius.com"
    private const val apiURL: String = "https://api.genius.com"
    private val accessToken = MainConfig.getString("geniusapi/access_token")

    fun getLyricsFor(query: String): GeniusLyrics? {
        if(accessToken.isEmpty()) return null

        val lyrics = GeniusLyrics(200L, "", "")

        val httpClient = OkHttpClient()
        val request: Request = Request.Builder().url("$apiURL/search?q=$query").addHeader("Authorization", "Bearer $accessToken").get().build()
        val response: Response = httpClient.newCall(request).execute()

        val jsonParser = JSONParser()
        val jsonWrapper: JSONObject = jsonParser.parse(response.body!!.string()) as JSONObject
        val meta: JSONObject = jsonWrapper["meta"] as JSONObject

        // Catch errors
        val metaStatus: Long = meta["status"] as Long
        if(metaStatus >= 400L) {
            val errorMessage: String = meta["error"] as String
            lyrics.errorMessage = errorMessage
            lyrics.responseCode = metaStatus
            return lyrics
        }

        val songResults: JSONArray = (jsonWrapper["response"] as JSONObject)["hits"] as JSONArray
        var song: JSONObject? = null

        for(songObject in songResults){
            val type: String = (songObject as JSONObject)["type"] as String

            if(type == "song"){
                song = songObject
                break
            }
        }

        if(song == null) {
            lyrics.responseCode = 404L
            return lyrics
        }

        val lyricsPath: String = (song["result"] as JSONObject)["path"] as String
        val pageDoc = Jsoup.connect("$baseURL$lyricsPath").get()

        return try {
            val lyricsElement: Element = pageDoc.selectFirst("div.lyrics")
            var lyricsPlain: String = Jsoup.parse(lyricsElement
                    .html()
                    .replace(Regex("(?i)<br[^>]*>"), "br2n")).wholeText()
            lyricsPlain = lyricsPlain.replace("br2n", "\n")

            lyrics.text = lyricsPlain
            lyrics
        } catch (ex: Exception) {
            lyrics
        }
    }
}