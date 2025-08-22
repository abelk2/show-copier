package eu.abelk.showcopier.domain.radarr

import kotlinx.serialization.Serializable

@Serializable
data class RadarrTag(
    val id: Int,
    val label: String
)
