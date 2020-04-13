package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.Sender

class CmdHelp : Command("help") {

    override fun execute(sender: Sender, args: ArrayList<String>) {
        sender.sendText("Executing command help...")!!.subscribe()
    }

}