package eu.abelk.showcopier.domain.copy

data class MovieToCopy(
    val id: Int,
    override val path: String
): Copyable
