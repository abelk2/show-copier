package eu.abelk.showcopier.domain.radarr

import kotlinx.serialization.Serializable

@Serializable
data class RadarrMovie(
    val id: Int,
    val tags: Set<Int>,
    val hasFile: Boolean,
    val movieFile: RadarrMovieFile? = null
)
