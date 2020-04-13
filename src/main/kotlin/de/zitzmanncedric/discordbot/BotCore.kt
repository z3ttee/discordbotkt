package de.zitzmanncedric.discordbot

import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.handler.ConsoleHandler
import de.zitzmanncedric.discordbot.config.MainConfig
import de.zitzmanncedric.discordbot.listener.MessageEventListener
import de.zitzmanncedric.discordbot.listener.ReadyEventListener
import discord4j.core.DiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val logger: Logger = LoggerFactory.getLogger(BotCore::class.java)

fun main(args: Array<String>) {
    logger.info("main(): Bot is now starting...")
    var token = ""

    try {
        for ((index, arg) in args.withIndex()) {
            if (arg.equals("-token", true)) {
                token = args[index + 1]
            }
        }
    } catch (ex: ArrayIndexOutOfBoundsException){
        logger.error("main(): Some attributes are missing values. Make sure to use a format like '-ATTR_NAME YOUR_VALUE'.")
    }

    if(token.isEmpty()) {
        logger.error("main(): A token is needed. Otherwise the bot cannot authenticate on Discord.")
        logger.error("main(): So without a token the bot will not work. Bot is shutting down now...")
        return
    }

    BotCore(token)
}

class BotCore(token: String) {
    companion object {
        var discordClient: DiscordClient? = null
    }

    init {
        discordClient = DiscordClient.create(token)

        // loading configs
        MainConfig.create()

        // registering commands
        ConsoleHandler.start()
        CommandHandler.registerCommands()

        // registering events
        discordClient!!.eventDispatcher.on(ReadyEvent::class.java).subscribe { (ReadyEventListener()::onReady)(it) }
        discordClient!!.eventDispatcher.on(MessageCreateEvent::class.java).subscribe { (MessageEventListener()::onMessage)(it) }
        discordClient!!.eventDispatcher.on(MessageUpdateEvent::class.java).subscribe { (MessageEventListener()::onMessageUpdated)(it) }

        // logging in
        discordClient!!.login().block()
    }
}