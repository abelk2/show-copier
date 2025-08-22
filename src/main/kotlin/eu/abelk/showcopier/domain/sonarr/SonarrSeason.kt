package eu.abelk.showcopier.domain.sonarr

import kotlinx.serialization.Serializable

@Serializable
data class SonarrSeason(
    val seasonNumber: Int,
    val statistics: SonarrSeasonStatistics
)
