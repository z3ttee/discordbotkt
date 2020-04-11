package de.zitzmanncedric.discordbot.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.lang.NullPointerException
import java.util.*
import kotlin.concurrent.schedule

abstract class ConfigHelper(private val name: String, private var filePath: String, private val version: Int) {
    private val logger: Logger = LoggerFactory.getLogger(ConfigHelper::class.java)

    init {
        filePath = System.getProperty("user.dir")+File.separator+"de/zitzmanncedric/discordbot/config"+File.separator+filePath
        create()
    }

    abstract fun onCreate(file: File?)
    abstract fun onCreateFailed(file: File)
    abstract fun onUpgrade(file: File)

    fun getValue(path: String): Any? {


        return null
    }
    fun putValue(path: String, value: Any){

    }

    protected fun addConfigLine(path: String, value: Any){

    }
    fun create() {
        val directory = File(filePath)
        val file = File(filePath, name)

        if(!file.exists()) {
            logger.info("create(): File '$name' not found in directory '$filePath'. Creating it...")

            val dirCreated: Boolean = directory.mkdirs()
            if (dirCreated || directory.exists()) {
                val fileCreated: Boolean = file.createNewFile()
                logger.info("create(): Created file '$name' successfully.")
                if (fileCreated) {
                    val writer = FileWriter(file)
                    writer.write("configVersion: \"$version\"")
                    writer.flush()
                    writer.close()

                    logger.info("create(): $file")
                    Timer("CreateFile", false).schedule(0) {
                        onCreate(file)
                    }
                }
            }

            if(!file.exists()){
                logger.warn("create(): Creating file '$name' failed.")
                onCreateFailed(file)
            }
        } else {
            onTryLoad(file)
        }
    }

    private fun onTryLoad(file: File) {
        try {
            val inputStream = FileInputStream(file)
            val buffedInS = BufferedInputStream(inputStream)

            val yaml = Yaml()
            val entries: Map<String, Any> = yaml.load(buffedInS)

            inputStream.close()
            buffedInS.close()
            entries.size // Test for nullpointer

            logger.info("onTryLoad(): File '${file.name}' is ready.")
        } catch (ex: NullPointerException){
            logger.warn("onTryLoad(): File '${file.name}' cannot be loaded.")
        }
    }
}