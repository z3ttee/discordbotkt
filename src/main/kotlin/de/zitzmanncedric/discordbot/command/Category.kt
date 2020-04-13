package de.zitzmanncedric.discordbot.command

enum class Category(val id: Int, val title: String, val description: String, val emoji: String) {

    GENERAL(1, "Allgemein", "Allgemeine Befehle", ":calling:"),
    MUSIC(2, "Musik & DJ", "Befehle zum Steuern des Musik", ":musical_note:"),
    HIDDEN(3, "Admin", "", "");

}