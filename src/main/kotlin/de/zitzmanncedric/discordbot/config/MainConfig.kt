package de.zitzmanncedric.discordbot.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

class MainConfig(name: String, filePath: String, version: Int) : ConfigHelper(name, filePath, version) {
    private val logger: Logger = LoggerFactory.getLogger(MainConfig::class.java)

    override fun onCreate(file: File?) {
        if(file != null) {
            val inputStream = FileInputStream(file)
            val bufferdInS = BufferedInputStream(inputStream)

            val yaml = Yaml()
            val entries: Map<String, Any> = yaml.load(bufferdInS)

            inputStream.close()
            bufferdInS.close()
        }
    }

    override fun onCreateFailed(file: File) {}
    override fun onUpgrade(file: File) {}
    override fun onDowngrade(file: File) {}
}