package de.zitzmanncedric.discordbot.command

enum class Category(val id: Int, val title: String, val description: String, val emoji: String) {

    GENERAL(1, "Allgemein", "Allgemeine Befehle", ":calling:"),
    MUSIC(2, "Musik & DJ", "Befehle zum Steuern des Musikbots", ":musical_note:"),
    FUN(3, "Fun", "", ":zany_face:"),
    VOTING(4, "Abstimmung", "", ":ballot_box_with_check:"),
    HIDDEN(5, "Admin", "", "");

}