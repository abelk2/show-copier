package eu.abelk.showcopier.service

import eu.abelk.showcopier.domain.storage.CopiedShows
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class StorageService(
    private val json: Json,
    private val copiedShowsFile: File
) {
    fun loadShows(): CopiedShows {
        return if (copiedShowsFile.exists()) {
            copiedShowsFile.inputStream().use { stream ->
                json.decodeFromStream(stream)
            }
        } else {
            CopiedShows(
                sonarrShows = emptyMap(),
                radarrShows = emptySet()
            ).also {
                saveShows(it)
            }
        }
    }

    fun saveShows(shows: CopiedShows) {
        copiedShowsFile.outputStream().use { stream ->
            json.encodeToStream(shows, stream)
        }
    }

    fun mergeShows(shows: CopiedShows) =
        saveShows(shows.mergeWith(loadShows()))
}