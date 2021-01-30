package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import discord4j.rest.util.Color
import java.util.function.Consumer
import kotlin.random.Random

class CmdCock: Command("cock", "(@Mention) (@Mention...)", "", Category.FUN) {
    private val maxLength: Int = 22
    private val minLength: Int = 1

    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {

        if(args.size == 0 || args.size == 1){
            if(args.size == 1 && message!!.userMentions.blockFirst() == null) {
                sender.sendError(Lang.getString("error_mention_required")).subscribe()
                return
            }

            val member: Member = when(args.size == 1) {
                true -> message!!.userMentions.blockFirst()!!.asMember(guild!!.id).block()!!
                else -> message!!.authorAsMember.block()!!
            }
            val cock = calculateLength()

            sender.sendTextWitEmbed(Lang.getString("headline_cock_comp"), Consumer {
                embed -> run {
                    embed.setColor(Color.PINK)
                    embed.setTitle(Lang.getString("paragraph_who_has_mightiest"))
                    embed.addField(Lang.getString("paragraph_cock_result").replace("%mention%", member.username).replace("%length%", (cock.length-2).toString()), cock, false)
                }
            }).subscribe()
            return
        }

        if(args.size > 1){
            if(message!!.userMentions.blockFirst() == null) {
                sender.sendError(Lang.getString("error_mention_required")).subscribe()
                return
            }

            val members = message.userMentions.buffer().blockFirst()!!
            val cocks = HashMap<User, String>()

            members.forEach {
                val cock = calculateLength()
                cocks[it] = cock
            }

            sender.sendTextWitEmbed(Lang.getString("headline_cock_comp"), Consumer {
                embed -> run {
                embed.setColor(Color.PINK)
                embed.setTitle(Lang.getString("paragraph_who_has_mightiest"))

                cocks.keys.forEach {
                    val cock = cocks[it]
                    embed.addField(Lang.getString("paragraph_cock_result").replace("%mention%", it.username).replace("%length%", (cock!!.length-2).toString()), cock, false)
                }
            }
            }).subscribe()
        }
    }

    private fun calculateLength(): String {
        var cock = "8"
        val length: Int = Random.nextInt(minLength, maxLength)
        for(i in 0 until length) {
            cock += "="
        }
        cock += "D"
        return cock
    }
}