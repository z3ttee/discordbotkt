package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.VoiceChannel
import kotlin.collections.ArrayList
import kotlin.random.Random

class CmdPick: Command("pick", "", "Wählt einen Spieler im Sprachkanal", Category.VOTING) {
    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        sender as DiscordSender

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

        channel.voiceStates.buffer().subscribe {
            val states: ArrayList<VoiceState> = ArrayList(it)

            var pickedMember: Member?
            do {
                val rndIndex = Random.nextInt(states.size)
                val pickedState = states[rndIndex]
                pickedMember = pickedState.member.block()
            } while (pickedMember!!.isBot)

            sender.sendText(Lang.getString("paragraph_member_picked").replace("%mention%", pickedMember.mention)).subscribe()
        }
    }
}