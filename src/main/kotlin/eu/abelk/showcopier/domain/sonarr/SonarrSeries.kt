package eu.abelk.showcopier.domain.sonarr

import kotlinx.serialization.Serializable

@Serializable
data class SonarrSeries(
    val id: Int,
    val seasons: Set<SonarrSeason>,
    val tags: Set<Int>
)