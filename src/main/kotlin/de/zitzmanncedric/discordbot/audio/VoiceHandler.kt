package de.zitzmanncedric.discordbot.audio

import de.zitzmanncedric.discordbot.BotCore
import de.zitzmanncedric.discordbot.language.Lang
import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.entity.TextChannel
import discord4j.core.`object`.entity.VoiceChannel
import discord4j.voice.VoiceConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object VoiceHandler {
    private val logger: Logger = LoggerFactory.getLogger(VoiceHandler::class.java)

    val activeConnections: HashMap<Guild, VoiceConnection> = HashMap()
    val textChannels: HashMap<Guild, MessageChannel> = HashMap()

    fun createConnection(guild: Guild, channel: VoiceChannel, textChannel: MessageChannel): Boolean {
        try {
            channel.join { joinSpec ->
                run {
                    joinSpec.setProvider(BotCore.provider!!)
                }
            }.subscribe { connection ->
                run {
                    activeConnections[guild] = connection
                    textChannels[guild] = textChannel

                    // TODO
                    Messages.sendText(Lang.getString("channel_joined"), textChannel).subscribe()
                    logger.info("createConnection(): Voice connection established.")
                }
            }
            return true
        } catch (ex: Exception){
            ex.printStackTrace()
            return false
        }
    }
    fun closeConnection(guild: Guild) {
        if(activeConnections.containsKey(guild)) {
            val connection: VoiceConnection = activeConnections.remove(guild)!!
            connection.disconnect().also {
                Messages.sendText(Lang.getString("channel_left"), getTextChannel(guild)!!)
                // TODO: Clear and stop music queue
            }
        }
        if(textChannels.containsKey(guild)) {
            textChannels.remove(guild)
        }
    }

    fun hasConnection(guild: Guild): Boolean {
        return activeConnections.containsKey(guild)
    }

    fun getTextChannel(guild: Guild): MessageChannel? {
        return textChannels[guild]
    }
    fun setTextChannel(guild: Guild, channel: MessageChannel) {
        textChannels[guild] = channel
    }

}