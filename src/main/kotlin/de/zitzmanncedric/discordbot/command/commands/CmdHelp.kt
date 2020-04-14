package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import discord4j.core.`object`.entity.Guild
import java.awt.Color
import java.util.function.Consumer

class CmdHelp : Command("help", "", "Zeigt den Hilfecenter", Category.GENERAL) {

    override fun execute(sender: Sender, guild: Guild, args: ArrayList<String>) {

        if(sender is DiscordSender){
            sender.private().sendTextWitEmbed("Hilfecenter & Befehlübersicht", Consumer { embed -> run {
                embed.setDescription("Hier ist die von dir angeforderte Übersicht über alle Befehle")

                for(category in Category.values()) {
                    val title = "${category.emoji} ${category.title}"
                    var content = ""
                    // TODO: Add prefix

                    val commands = ArrayList<Command>(CommandHandler.commands.values.filter { it.category == category })
                    if(commands.isEmpty()){
                        content = "Keine Befehle für diese Kategorie gefunden."
                    } else {
                        for(command in commands) {
                            val usage = when (command.usage.isEmpty() || command.usage.isBlank()) {
                                true -> ""
                                else -> "${command.usage} "
                            }

                            content += "` ${command.name} $usage`: ${command.description} \n"
                        }
                    }

                    embed.addField(title, content, false)
                }

            }}).subscribe()

            return
        }

        sender.sendText("[]====== Helpcenter ======[]")

        for(category in Category.values()) {
            sender.sendText(">> ${category.title}")

            for(command in CommandHandler.commands.values){
                if(command.category == category){
                    val usage = when(command.usage.isEmpty() || command.usage.isBlank()) {
                        true -> ""
                        else -> "${command.usage} "
                    }

                    sender.sendText("${command.name} $usage- ${command.description}")
                }
            }

            sender.sendText("")
        }
    }

}