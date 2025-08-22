package eu.abelk.showcopier.domain.sonarr

import kotlinx.serialization.Serializable

@Serializable
data class SonarrTag(
    val id: Int,
    val label: String
)
