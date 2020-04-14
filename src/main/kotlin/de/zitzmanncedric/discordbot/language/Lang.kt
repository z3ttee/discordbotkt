package de.zitzmanncedric.discordbot.language

import de.zitzmanncedric.discordbot.BotCore
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.nio.charset.Charset
import java.nio.file.Files
import javax.xml.parsers.DocumentBuilderFactory

object Lang {
    private val logger: Logger = LoggerFactory.getLogger(Lang::class.java)

    private val strings: HashMap<String, String> = HashMap()
    private var language: Language? = null

    fun getString(itemName: String): String {
        return strings.getOrDefault(itemName, "")
    }

    fun initialize(l: Language) {
        logger.info("initialize(): Loading language file...")
        language = l

        val fileName = "languages/${language!!.title}.xml"
        val inputStr = BotCore::class.java.classLoader.getResourceAsStream(fileName)

        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStr)
        inputStr!!.close()
        xmlDoc.documentElement.normalize()

        val stringsXml = xmlDoc.documentElement.getElementsByTagName("item")
        for(i in 0 until stringsXml.length) {
            val itemNode: Node = stringsXml.item(i)

            val nodeName = itemNode.attributes.item(0).nodeValue
            val nodeContent = itemNode.textContent

            strings[nodeName] = nodeContent
        }
    }
}

enum class Language(val id: Int, val title: String) {
    DE(1, "de-DE"),
    EN(2, "en-EN");
}