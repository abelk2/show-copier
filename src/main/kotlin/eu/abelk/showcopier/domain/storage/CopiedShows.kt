package eu.abelk.showcopier.domain.storage

import eu.abelk.showcopier.domain.copy.Copyable
import eu.abelk.showcopier.domain.copy.EpisodeToCopy
import eu.abelk.showcopier.domain.copy.MovieToCopy
import kotlinx.serialization.Serializable

@Serializable
data class CopiedShows(
    val sonarrShows: Map<Int, SonarrShow>,
    val radarrShows: Set<Int>
) {
    fun mergeWith(other: CopiedShows): CopiedShows {
        val mergedShows = sonarrShows.toMutableMap()
        other.sonarrShows.forEach { seriesId, series ->
            mergedShows.merge(seriesId, series) { series1: SonarrShow, series2: SonarrShow ->
                val mergedSeasons = series1.copiedSeasons.toMutableMap()
                series2.copiedSeasons.forEach { seasonNumber, episodeIds ->
                    mergedSeasons.merge(seasonNumber, episodeIds) { episodeIds1, episodeIds2 ->
                        episodeIds1 + episodeIds2
                    }
                }
                SonarrShow(mergedSeasons)
            }
        }
        return CopiedShows(
            sonarrShows = mergedShows,
            radarrShows = radarrShows + other.radarrShows
        )
    }

    companion object {
        fun createFrom(copyables: List<Copyable>) =
            CopiedShows(
                sonarrShows = copyables
                    .filter { it is EpisodeToCopy }
                    .map { it as EpisodeToCopy }
                    .groupBy { it.seriesId }
                    .mapValues { (_, episodes) ->
                        SonarrShow(
                            episodes
                                .groupBy { it.seasonNumber }
                                .mapValues { (_, seasonEpisodes) ->
                                    seasonEpisodes
                                        .map { it.episodeId }
                                        .toSet()
                                }
                        )
                    },
                radarrShows = copyables
                    .filter { it is MovieToCopy }
                    .map { it as MovieToCopy }
                    .map { it.id }
                    .toSet()
            )
    }
}
