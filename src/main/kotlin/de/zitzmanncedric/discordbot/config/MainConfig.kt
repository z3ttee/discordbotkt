package de.zitzmanncedric.discordbot.config

import java.io.File

object MainConfig : ConfigHelper("config.yml", "", 1) {

    override fun onCreate(file: File?) {
        putValue("general", "prefix", "ss ")
        putValue("general", "nickname", "SyndicateBot")
    }

    override fun onCreateFailed(file: File) {}
    override fun onUpgrade(file: File, prevVersion: Int, newVersion: Int) {}
    override fun onDowngrade(file: File, prevVersion: Int, newVersion: Int) {}
}