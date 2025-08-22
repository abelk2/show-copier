package eu.abelk.showcopier.service

import eu.abelk.showcopier.client.RadarrClient
import eu.abelk.showcopier.domain.copy.MovieToCopy
import eu.abelk.showcopier.logging.logger
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet

class MoviesService(
    private val radarrClient: RadarrClient,
    private val storageService: StorageService,
    private val tag: String
) {
    private val logger = logger()

    suspend fun getMoviesToCopy(): Set<MovieToCopy> {
        val copiedMovies = storageService.loadShows().radarrShows
        val tagId = lookUpTagId()
        return radarrClient.getMovies()
            .asFlow()
            .filter { tagId in it.tags && it.hasFile && it.movieFile != null }
            .filterNot { it.id in copiedMovies }
            .map {
                MovieToCopy(
                    id = it.id,
                    path = it.movieFile!!.path
                ).also {
                    logger.info("Found movie to copy: $it")
                }
            }
            .toSet()
    }

    private suspend fun lookUpTagId() =
        radarrClient.getTags()
            .asFlow()
            .first { it.label == tag }
            .id
}
