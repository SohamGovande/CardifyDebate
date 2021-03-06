package me.sohamgovande.cardr.data.files

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import me.sohamgovande.cardr.CardrDesktop
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object CardrFileSystem {

    private val logger = LogManager.getLogger(CardrFileSystem::class.java)
    private val foldersJsonPath = Paths.get(System.getProperty("cardr.data.dir"), "Cards", "Folders.json")

    val folders = mutableListOf<FSFolder>()
    val cards = mutableListOf<FSCardData>()

    private val gson = GsonBuilder().setPrettyPrinting().setLenient().create()

    fun read() {
        val folder = Paths.get(System.getProperty("cardr.data.dir"), "Cards")
        try { Files.createDirectories(folder) } catch (e: FileAlreadyExistsException) { }

        // Load all folders
        if (Files.exists(foldersJsonPath)) {
            val foldersArray = JsonParser().parse(String(Files.readAllBytes(foldersJsonPath))).asJsonArray
            for (folderRawObj in foldersArray) {
                val parsedFolder: FSFolder? = gson.fromJson(folderRawObj, FSFolder::class.java)
                if (parsedFolder != null)
                    folders.add(parsedFolder)
                else
                    logger.error("Unable to parse folder from json data $folderRawObj")
            }
        } else {
            folders.add(FSFolder(CardrDesktop.CURRENT_VERSION_INT, "Uncategorized", mutableListOf()))
            saveFolders()
        }

        // Load all cards
        val cardFiles = folder.toFile().listFiles { _, name -> name.toLowerCase().endsWith(".card") }
        if (cardFiles != null) {
            for (cardFile in cardFiles) {
                val cardDataParsed = JsonParser().parse(String(Files.readAllBytes(Paths.get(cardFile.toURI())))).asJsonObject
                val cardData: FSCardData? = gson.fromJson(cardDataParsed, FSCardData::class.java)

                if (cardData != null) {
                    cardData.filename = cardFile.nameWithoutExtension
                    cardData.setProperties(cardDataParsed["properties"].asJsonObject)
                    cards.add(cardData)
                } else {
                    logger.error("Unable to parse card from json data $cardData")
                }
            }
        }
    }

    fun getTopLevelFolders(): List<FSFolder> {
        return folders.filter { it.isRootFolder() }
    }

    fun saveFolders() {
        val jsonArray = JsonArray()
        for (folder in  folders)
            jsonArray.add(gson.toJsonTree(folder, FSFolder::class.java))
        Files.write(
            foldersJsonPath,
            gson.toJson(jsonArray).toByteArray()
        )
    }

    fun saveCard(card: FSCardData) {
        if (card.filename == null || card.filename == "")
            card.filename = card.uuid.toString()
        val jsonParsed = gson.toJsonTree(card, FSCardData::class.java).asJsonObject
        jsonParsed.add("properties", card.getPropertiesJson())
        Files.write(
            Paths.get(System.getProperty("cardr.data.dir"), "Cards", "${card.filename}.card"),
            gson.toJson(jsonParsed).toByteArray()
        )
    }

    fun deleteCard(card: FSCardData, removeFromFolders: Boolean): Boolean {
        cards.remove(card)
        if (removeFromFolders) {
            for (folder in findFolders(card))
                folder.removeCard(card.uuid)
            saveFolders()
        }
        return Files.deleteIfExists(Paths.get(System.getProperty("cardr.data.dir"), "Cards", "${card.filename}.card"))
    }

    fun findCard(uuid: UUID): FSCardData = cards.first { it.uuid == uuid }
    fun findFolders(card: FSCardData): List<FSFolder> = folders.filter { it.cardUUIDs.contains(card.uuid) }
    fun findFolder(path: String): FSFolder? = folders.firstOrNull { it.path == path }
}
