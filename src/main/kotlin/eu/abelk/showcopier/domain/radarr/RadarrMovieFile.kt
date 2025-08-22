package eu.abelk.showcopier.domain.radarr

import kotlinx.serialization.Serializable

@Serializable
data class RadarrMovieFile(
    val path: String
)
