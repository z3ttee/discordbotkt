package de.zitzmanncedric.discordbot.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

abstract class ConfigHelper(private val name: String, private var filePath: String, private val version: Int) {
    private val logger: Logger = LoggerFactory.getLogger(ConfigHelper::class.java)

    abstract fun onCreate(file: File?)
    abstract fun onCreateFailed(file: File)
    abstract fun onUpgrade(file: File, prevVersion: Int, newVersion: Int)
    abstract fun onDowngrade(file: File, prevVersion: Int, newVersion: Int)

    init {
        filePath = System.getProperty("user.dir")+File.separator+"config"+File.separator+filePath
        val file = File(filePath, name)
        if(file.exists()) {
            try {
                val prevVersion: Int = getInt("configVersion")
                if (prevVersion < version) {
                    logger.info("init(): Newer version of '$name' is required. Upgrading file...")
                    onUpgrade(file, prevVersion, version)
                }
                if (prevVersion > version){
                    logger.info("init(): Older version of '$name' is required. Downgrading file...")
                    onDowngrade(file, prevVersion, version)
                }

                putValue("", "configVersion", version)
            } catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        create()
    }

    /**
     * Gets the specific value of a line in the file
     */
    fun getValue(path: String): String? {
        val entries = loadContent()
        return processPath(path, entries)
    }

    /**
     * Process given path and map to find the requested value
     */
    private fun processPath(path: String, map: HashMap<*, *>): String? {
        val pathKeys = path.split("/")
        var nextMap = map
        var value: Any? = null

        for (key in pathKeys) {
            val obj = nextMap[key]
            if(obj is HashMap<*, *>) {
                nextMap = obj
            } else {
                value = obj
                break
            }
        }
        return value.toString()
    }

    /**
     * Gets the specific int-value of a line in the file
     */
    fun getInt(path: String): Int {
        return getValue(path)!!.toInt()
    }

    /**
     * Gets the specific string-value of a line in the file
     */
    fun getString(path: String): String {
        return getValue(path).toString()
    }

    /**
     * Gets the specific bool-value of a line in the file
     */
    fun getBoolean(path: String): Boolean {
        return getValue(path)!!.toBoolean()
    }

    /**
     * Gets the specific float-value of a line in the file
     */
    fun getFloat(path: String): Float {
        return getValue(path)!!.toFloat()
    }


    /**
     * Puts a new line inside the file. (Only two level put possible)
     */
    fun putValue(category: String, name: String, v: Any){
        val entries = loadContent()

        if(category.isEmpty() || category.isBlank()) {
            entries[name] = v
            return
        }

        if(!entries.containsKey(category)){
            entries[category] = HashMap<String, Any>()
        }

        val categoryMap = entries[category] as HashMap<String, Any>
        if(!categoryMap.containsKey(name)){
            categoryMap[name] = v
        }

        save(entries)
    }

    private fun save(entries: HashMap<String, Any>){
        val dumpOptions = DumperOptions()
        dumpOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        dumpOptions.isPrettyFlow = true

        val yaml = Yaml(dumpOptions)
        yaml.dump(entries,FileWriter(File(filePath, name)))
    }

    /**
     * Creates a new file. Usually triggered in BotCore.kt when initializing the bot
     */
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
                    writer.write("configVersion: $version")
                    writer.flush()
                    writer.close()

                    Thread.sleep(10)
                    onCreate(file)
                }
            }

            if(!file.exists()){
                logger.warn("create(): Creating file '$name' failed.")
                onCreateFailed(file)
            }
        }
    }

    private fun loadContent(): HashMap<String, Any> {
        val file = File(filePath, name)

        val inputStream = FileInputStream(file)
        val bufferdInS = BufferedInputStream(inputStream)

        val yaml = Yaml()
        var entries = HashMap<String, Any>()

        try {
            entries = yaml.load(bufferdInS)
        } catch (ex: Exception){
            logger.error("loadContent(): Error occured loading file contents:")
            ex.printStackTrace()
        } finally {
            inputStream.close()
            bufferdInS.close()
        }

        return entries
    }
}