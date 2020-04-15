package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.ConsoleSender
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CmdJoin: Command("join", "(channel)", "Lässt den Bot in einen Sprachkanal joinen", Category.MUSIC) {
    private val logger: Logger = LoggerFactory.getLogger(CmdJoin::class.java)

    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        if(sender is ConsoleSender) {
            sender.sendError("This command is not available for console input.")
            return
        }

        // Make sender a discord sender
        sender as DiscordSender

        if(args.size > 1) {
            sendUsage(sender)
            return
        }

        if(VoiceHandler.hasConnection(guild!!)) {
            sender.sendError(Lang.getString("channel_already_connected")).subscribe()
            return
        }

        if(args.size == 0){
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

            VoiceHandler.createConnection(guild, channel, message!!.channel.block()!!).subscribe {
                if(it) sender.sendText(Lang.getString("channel_joined")).subscribe()
            }
        } else {
            val channelName: String = args[0]

            // TODO: Check if channel exists
            val availableChannels = guild.channels.filter { it.type == Channel.Type.GUILD_VOICE }.filter { it.name == channelName }
            val channel = availableChannels.blockFirst()

            if(channel == null) {
                sender.sendError(Lang.getString("error_voice_not_found")).subscribe()
            } else {
                VoiceHandler.createConnection(guild, channel as VoiceChannel, message!!.channel.block()!!).subscribe {
                    if(it) sender.sendText(Lang.getString("channel_joined")).subscribe()
                }
            }
        }
    }
}