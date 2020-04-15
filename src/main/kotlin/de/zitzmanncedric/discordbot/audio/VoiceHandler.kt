package de.zitzmanncedric.discordbot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import de.zitzmanncedric.discordbot.audio.queue.GuildQueueManager
import de.zitzmanncedric.discordbot.language.Lang
import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.entity.VoiceChannel
import discord4j.voice.VoiceConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.net.URL

object VoiceHandler {
    private val logger: Logger = LoggerFactory.getLogger(VoiceHandler::class.java)

    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()

    val activeConnections: HashMap<Guild, VoiceConnection> = HashMap()
    val queueManagers: HashMap<Guild, GuildQueueManager> = HashMap()
    val textChannels: HashMap<Guild, MessageChannel> = HashMap()

    fun createConnection(guild: Guild, channel: VoiceChannel, textChannel: MessageChannel): Mono<Boolean> {
        return Mono.create {
            try {
                queueManagers[guild] = GuildQueueManager(guild, playerManager.createPlayer())

                val connection: VoiceConnection? = channel.join { joinSpec ->
                    run {
                        joinSpec.setProvider(getQueueManager(guild)!!.audioProvider)
                    }
                }.block()

                if(connection != null) {
                    activeConnections[guild] = connection
                    textChannels[guild] = textChannel

                    logger.info("createConnection(): Voice connection established.")
                    it.success(true)
                } else {
                    it.success(false)
                }
            } catch (ex: Exception){
                ex.printStackTrace()
                it.success(false)
            }
        }
    }
    fun closeConnection(guild: Guild): Mono<Boolean> {
        return Mono.create {
            if (activeConnections.containsKey(guild)) {
                val connection: VoiceConnection = activeConnections.remove(guild)!!
                connection.disconnect().also {
                    Messages.sendText(Lang.getString("channel_left"), getTextChannel(guild)!!)
                    // TODO: Clear and stop music queue
                }
            }
            textChannels.remove(guild)
            queueManagers.remove(guild)

            it.success(true)
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
    fun getQueueManager(guild: Guild): GuildQueueManager? {
        return queueManagers[guild]
    }

    fun playSource(guild: Guild, url: URL) {
        playerManager.loadItem(url.toString(), getQueueManager(guild))
    }

}