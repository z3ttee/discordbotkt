package de.zitzmanncedric.discordbot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import de.zitzmanncedric.discordbot.BotCore
import de.zitzmanncedric.discordbot.audio.queue.AudioResultHandler
import de.zitzmanncedric.discordbot.language.Lang
import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.entity.TextChannel
import discord4j.core.`object`.entity.VoiceChannel
import discord4j.voice.VoiceConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.net.URL

object VoiceHandler {
    private val logger: Logger = LoggerFactory.getLogger(VoiceHandler::class.java)

    val activeConnections: HashMap<Guild, VoiceConnection> = HashMap()
    val textChannels: HashMap<Guild, MessageChannel> = HashMap()

    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val audioPlayer: AudioPlayer = playerManager.createPlayer()
    val audioProvider: discord4j.voice.AudioProvider = AudioProvider(audioPlayer)

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
        //TODO: AudioSourceManagers.registerLocalSource(playerManager)
    }

    fun createConnection(guild: Guild, channel: VoiceChannel, textChannel: MessageChannel): Mono<Boolean> {
        return Mono.create {
            try {
                val connection: VoiceConnection? = channel.join { joinSpec ->
                    run {
                        joinSpec.setProvider(audioProvider)
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
            if (textChannels.containsKey(guild)) {
                textChannels.remove(guild)
            }

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

    fun playSource(guild: Guild, url: URL) {
        val resultHandler: AudioResultHandler = AudioResultHandler(guild, audioPlayer)
        playerManager.loadItem(url.toString(), resultHandler)
    }

}