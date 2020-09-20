package de.zitzmanncedric.discordbot.api

import de.zitzmanncedric.discordbot.config.MainConfig
import discord4j.core.`object`.entity.Member
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import reactor.core.publisher.Mono
import java.net.ConnectException

object TSMedia {

    private val apiKey: String = MainConfig.getString("tsmedia/apiKey")
    private val baseURL: String = MainConfig.getString("tsmedia/baseURL")+"/"+MainConfig.getString("tsmedia/version")

    fun sendUploadRequest(attachmentURL: String, member: Member): Mono<TSMediaResult> {
        return Mono.defer {

            var status = TSMediaStatus()
            var data = JSONObject()

            try {
                val httpClient = OkHttpClient()
                val request = Request.Builder()
                    .url(baseURL+"/video/upload/?url="+attachmentURL+"&creator="+member.id.asString())
                    .post(FormBody.Builder().build())
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()

                val call = httpClient.newCall(request)
                val response = call.execute()
                val body = response.body?.string()

                data = JSONParser().parse(body) as JSONObject

                val statusData = data.getOrDefault("status", JSONObject()) as JSONObject

                status = TSMediaStatus(statusData.getOrDefault("code", 400).toString().toInt(), statusData.getOrDefault("message", 400).toString())

                data = if(status.code == 200) {
                    data.getOrDefault("data", JSONObject()) as JSONObject
                } else {
                    JSONObject()
                }
            } catch (ex: Exception) {
                if(ex is ConnectException) {
                    status = TSMediaStatus(400, ex.message!!)
                }

                ex.printStackTrace()
            }

            return@defer Mono.just(TSMediaResult(status,data))
        }

    }

}