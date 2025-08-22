package eu.abelk.showcopier.domain.sonarr

import kotlinx.serialization.Serializable

@Serializable
data class SonarrEpisodeFile(
    val path: String
)
