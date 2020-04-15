package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.sender.ConsoleSender
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.VoiceChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

class CmdPlay: Command("play", "", Lang.getString("cmd_play_description"), Category.MUSIC) {
    private val logger: Logger = LoggerFactory.getLogger(CmdPlay::class.java)

    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        if(sender is ConsoleSender) {
            sender.sendError("This command is not available for console input.")
            return
        }

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

        if(args.size == 0){
            // TODO: Resume music
            return
        }

        if(args.size == 1) {
            val url = args[0]
            VoiceHandler.playSource(guild, URL(url))
            message!!.delete().subscribe()
            return
        }

        // TODO: Search on youtube

    }
}