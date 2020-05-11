package de.zitzmanncedric.discordbot.config

import java.io.File

object MainConfig : ConfigHelper("config.yml", "", 1) {

    override fun onCreate(file: File?) {
        putValue("general", "prefix", "ss ")
        putValue("general", "nickname", "SyndicateBot")
        putValue("geniusapi", "client_id", "INSERT_CLIENT_ID")
        putValue("geniusapi", "client_secret", "INSERT_SECRET")
        putValue("geniusapi", "access_token", "INSERT_TOKEN")
    }

    override fun onCreateFailed(file: File) {}
    override fun onUpgrade(file: File, prevVersion: Int, newVersion: Int) {}
    override fun onDowngrade(file: File, prevVersion: Int, newVersion: Int) {}
}