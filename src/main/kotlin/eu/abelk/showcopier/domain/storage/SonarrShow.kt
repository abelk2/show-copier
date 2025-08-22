package eu.abelk.showcopier.domain.storage

import kotlinx.serialization.Serializable

@Serializable
data class SonarrShow(
    val copiedSeasons: Map<Int, Set<Int>>
)
