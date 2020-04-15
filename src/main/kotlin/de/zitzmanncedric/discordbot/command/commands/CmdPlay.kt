package de.zitzmanncedric.discordbot.command.commands

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
import java.net.URL

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
        }

        if(args.size == 1) {
            val url = args[0]
            message!!.delete().subscribe()
            VoiceHandler.playSource(guild, URL(url))
            return
        }

        // TODO: Search on youtube
    }
}