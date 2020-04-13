package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender

class CmdHelp : Command("help", "", "Zeigt den Hilfecenter", Category.GENERAL) {

    override fun execute(sender: Sender, args: ArrayList<String>) {

        if(sender is DiscordSender){


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