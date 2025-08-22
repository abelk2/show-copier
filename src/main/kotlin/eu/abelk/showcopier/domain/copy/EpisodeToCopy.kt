package eu.abelk.showcopier.domain.copy

data class EpisodeToCopy(
    val seriesId: Int,
    val seasonNumber: Int,
    val episodeId: Int,
    override val path: String
): Copyable
