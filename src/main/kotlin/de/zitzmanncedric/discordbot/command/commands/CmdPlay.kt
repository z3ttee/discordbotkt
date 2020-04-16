package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.BotCore
import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.audio.queue.GuildQueueManager
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.VoiceChannel
import org.apache.commons.validator.routines.UrlValidator
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class CmdPlay: Command("play", "<url | query>", Lang.getString("cmd_play_description"), Category.MUSIC) {
    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        sender as DiscordSender

        if(!VoiceHandler.hasConnection(guild!!)) {
            val voiceState: VoiceState? = sender.member.voiceState.block()

            if(voiceState == null) {
                sender.sendError(Lang.getString("error_need_tobe_in_channel")).subscribe()
                return
            }

            val channel: VoiceChannel? = voiceState.channel.block()
            if(channel == null) {
                sender.sendError(Lang.getString("error_voice_not_found")).subscribe()
                return
            }

            if(VoiceHandler.createConnection(guild, channel, message!!.channel.block()!!).block()!!) {
                sender.sendText(Lang.getString("channel_joined")).subscribe()
            }
        }

        val queueManager: GuildQueueManager = VoiceHandler.getQueueManager(guild)!!

        if(args.size == 0 && queueManager.audioPlayer.isPaused){
            queueManager.audioPlayer.isPaused = false
            return
        } else {
            if(args.size == 0){
                sendUsage(sender)
                return
            }
        }

        if(queueManager.audioPlayer.isPaused) queueManager.audioPlayer.isPaused = false

        if(args.size >= 1) {
            message!!.delete().subscribe()

            if(args.size == 1) {
                val url = args[0]
                if(UrlValidator.getInstance().isValid(url)) {
                    VoiceHandler.playSource(guild, URL(url))
                    return
                }
            }

            if(!BotCore.ytSearchEnabled) {
                sender.sendError(Lang.getString("error_yt_deactivated")).subscribe()
                return
            }

            var query = ""
            args.forEach { arg -> run {
                if(query.isEmpty()) query += arg
                else query = "$query $arg"
            }}

            sender.sendText(Lang.getString("audio_searching_yt").replace("%query%", query)).subscribe {
                sender.channel!!.type().subscribe()
            }

            val callback : (String) -> Unit = {
                VoiceHandler.playSource(guild, URL(it))
            }

            val thread = Thread {
                try {
                    val url: String = BotCore.performYoutubeSearch(query, 1)
                    callback(url)
                } catch (e: Exception) {
                    sender.sendException(e).subscribe()
                    e.printStackTrace()
                }
            }

            thread.name = "yt-search-${UUID.randomUUID()}"
            thread.start()

        }
    }
}