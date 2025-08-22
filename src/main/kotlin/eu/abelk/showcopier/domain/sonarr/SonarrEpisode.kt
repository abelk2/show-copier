package eu.abelk.showcopier.domain.sonarr

import kotlinx.serialization.Serializable

@Serializable
data class SonarrEpisode(
    val id: Int,
    val hasFile: Boolean,
    val episodeFileId: Int
)
