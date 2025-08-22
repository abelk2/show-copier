package eu.abelk.showcopier.service

import eu.abelk.showcopier.client.SonarrClient
import eu.abelk.showcopier.domain.copy.EpisodeToCopy
import eu.abelk.showcopier.logging.logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet

class SeriesService(
    private val sonarrClient: SonarrClient,
    private val storageService: StorageService,
    private val tag: String
) {
    private val logger = logger()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getEpisodesToCopy(): Set<EpisodeToCopy> {
        val copiedShows = storageService.loadShows().sonarrShows
        val tagId = lookUpTagId()
        return sonarrClient.getSeries()
            .asFlow()
            .filter { tagId in it.tags }
            .flatMapConcat { series ->
                val copiedSeasons = copiedShows[series.id]?.copiedSeasons ?: emptyMap()
                series.seasons
                    .asFlow()
                    .flatMapConcat { season ->
                        val copiedEpisodes = copiedSeasons[season.seasonNumber] ?: emptySet()
                        if (copiedEpisodes.size < season.statistics.totalEpisodeCount) {
                            val episodes = sonarrClient.getEpisodes(series.id, season.seasonNumber)
                            episodes.asFlow()
                                .filter { it.hasFile }
                                .filterNot { it.id in copiedEpisodes }
                                .map { episode ->
                                    val episodeFile = sonarrClient.getEpisodeFile(episode.episodeFileId)
                                    EpisodeToCopy(
                                        seriesId = series.id,
                                        seasonNumber = season.seasonNumber,
                                        episodeId = episode.id,
                                        path = episodeFile.path
                                    ).also {
                                        logger.info("Found episode to copy: $it")
                                    }
                                }
                        } else {
                            emptyFlow()
                        }
                    }
            }
            .toSet()
    }

    private suspend fun lookUpTagId() =
        sonarrClient.getTags()
            .asFlow()
            .first { it.label == tag }
            .id
}