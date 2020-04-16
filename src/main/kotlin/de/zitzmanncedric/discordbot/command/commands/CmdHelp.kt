package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import java.util.function.Consumer

class CmdHelp : Command("help", "", Lang.getString("cmd_help_description"), Category.GENERAL, true) {

    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        if(sender is DiscordSender){
            sender.sendText(Lang.getString("paragraph_overview_sent")).subscribe()
            sender.private().sendTextWitEmbed(Lang.getString("headline_help"), Consumer { embed -> run {
                embed.setDescription(Lang.getString("paragraph_overview"))

                for(category in Category.values()) {
                    if(category != Category.HIDDEN) {
                        val title = "${category.emoji} ${category.title}"
                        var content = Lang.getString("error_not_found_in_category")
                        // TODO: Add prefix

                        val commands = ArrayList<Command>(CommandHandler.commands.values.filter { it.category == category })
                        if (commands.isNotEmpty()) {
                            content = ""

                            for (command in commands) {
                                val usage = when (command.usage.isEmpty()) {
                                    true -> ""
                                    else -> "${command.usage} "
                                }

                                content = "$content ` ${command.name} $usage`: ${command.description} \n"
                            }
                        }

                        embed.addField(title, content, false)
                    }
                }

            }}).subscribe()

            return
        }

        sender.sendText("[]====== Helpcenter ======[]")
        for(category in Category.values()) {
            sender.sendText(">> ${category.title}")

            for(command in CommandHandler.commands.values){
                if(command.category == category && command.consoleOptimised){
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