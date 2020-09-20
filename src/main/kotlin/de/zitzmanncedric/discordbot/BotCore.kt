package de.zitzmanncedric.discordbot

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.handler.ConsoleHandler
import de.zitzmanncedric.discordbot.config.MainConfig
import de.zitzmanncedric.discordbot.language.Lang
import de.zitzmanncedric.discordbot.language.Language
import de.zitzmanncedric.discordbot.listener.MessageEventListener
import de.zitzmanncedric.discordbot.listener.ReadyEventListener
import discord4j.core.DiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.security.GeneralSecurityException
import kotlin.system.exitProcess

private val logger: Logger = LoggerFactory.getLogger(BotCore::class.java)

fun main(args: Array<String>) {
    logger.info("main(): Bot is now starting...")
    var token = ""
    var ytKey = ""

    try {
        for ((index, arg) in args.withIndex()) {
            if (arg.equals("-token", true)) {
                token = args[index + 1]
            }
            if(arg.equals("-ytkey", true)) {
                ytKey = args[index+1]
            }
        }
    } catch (ex: ArrayIndexOutOfBoundsException){
        logger.error("main(): Some attributes are missing values. Make sure to use a format like '-ATTR_NAME YOUR_VALUE'.")
    }

    if(token.isEmpty) {
        logger.error("main(): A token is needed. Otherwise the bot cannot authenticate on Discord.")
        logger.error("main(): So without a token the bot will not work. Bot is shutting down now...")
        exitProcess(0)
    }

    if(ytKey.isEmpty) {
        logger.warn("main(): YT-Api-Key not found in command line arguments. Assuming that yt search with play command is not appreciated.")
        logger.warn("main(): YT-Search is not enabled.")
    }

    BotCore(token, ytKey)
}

class BotCore(token: String, ytapikey: String) {
    companion object {
        var discordClient: DiscordClient? = null
        var ytSearchEnabled: Boolean = false
        var ytkey : String = ""
        var avatarURL: String = ""

        private const val APPLICATION_NAME = "SyndicateBot Discord"
        private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()

        private fun getYoutubeService(): YouTube? {
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            return YouTube.Builder(httpTransport, JSON_FACTORY, null).setApplicationName(APPLICATION_NAME).build()
        }

        @Throws(GeneralSecurityException::class, IOException::class)
        fun performYoutubeSearch(args: String?, maxResults: Long): String {
            val service = getYoutubeService()
            val request = service!!.search().list("snippet").set("q", args).setKey(ytkey)
            val response = request.setMaxResults(maxResults).execute()

            if(response.items.isEmpty()) {
                throw Exception("No items in response")
            }

            val result = response.items[0]
            val playlistID = result.id.playlistId
            val videoID = result.id.videoId
            var playableUrl = "https://www.youtube.com/watch?v=$videoID"

            if (playlistID != null) {
                playableUrl += "&list=$playlistID"
            }

            return playableUrl
        }
    }

    init {
        ytkey = ytapikey
        ytSearchEnabled = ytkey.isNotEmpty()

        logger.info(PlayerLibrary.VERSION)

        // loading configs
        MainConfig.create()

        // load language
        // TODO: Make it an option via command and config
        Lang.initialize(Language.DE)

        // registering commands
        ConsoleHandler.start()
        CommandHandler.registerCommands()

        // creating instance of client
        discordClient = DiscordClient.create(token)

        // Getting avatarURL
        try {
            avatarURL = discordClient!!.self.block()!!.avatarUrl
        } catch (ignored: Exception){ }

        // registering events
        discordClient!!.eventDispatcher.on(ReadyEvent::class.java).subscribe { (ReadyEventListener()::onReady)(it) }
        discordClient!!.eventDispatcher.on(MessageCreateEvent::class.java).subscribe { (MessageEventListener()::onMessage)(it) }
        discordClient!!.eventDispatcher.on(MessageUpdateEvent::class.java).subscribe { (MessageEventListener()::onMessageUpdated)(it) }

        // logging in
        discordClient!!.login().block()
    }
}