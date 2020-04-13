package de.zitzmanncedric.discordbot.command

enum class Category(val id: Int, val title: String, val description: String) {

    GENERAL(1, "Allgemein", "Allgemeine Befehle"),
    MUSIC(2, "Musik & DJ", "Befehle zum Steuern des Musik"),
    HIDDEN(3, "Admin", "");

}