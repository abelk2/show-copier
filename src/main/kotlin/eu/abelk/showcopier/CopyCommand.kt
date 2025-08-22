package eu.abelk.showcopier

import eu.abelk.showcopier.domain.storage.CopiedShows
import eu.abelk.showcopier.logging.logger
import eu.abelk.showcopier.service.CopyService
import eu.abelk.showcopier.service.MoviesService
import eu.abelk.showcopier.service.SeriesService
import eu.abelk.showcopier.service.StorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CopyCommand(
    private val seriesService: SeriesService,
    private val moviesService: MoviesService,
    private val copyService: CopyService,
    private val storageService: StorageService,
    private val dryRun: Boolean
) {
    private val logger = logger()

    suspend fun run() {
        logger.info("Starting copy at ${Clock.System.now()}")

        logger.info("Fetching series episodes and movies to copy")
        val copyables = coroutineScope {
            listOf(
                async {
                    seriesService.getEpisodesToCopy()
                },
                async {
                    moviesService.getMoviesToCopy()
                }
            ).awaitAll().flatten()
        }

        if (copyables.isEmpty()) {
            logger.info("Nothing to copy")
        } else {
            logger.info("Copying ${copyables.size} series episodes and movies")
            coroutineScope {
                val limitedDispatcher = Dispatchers.IO.limitedParallelism(10)
                copyables.map { copyable ->
                    async(limitedDispatcher) {
                        copyService.copy(copyable.path)
                    }
                }.awaitAll()
            }

            if (dryRun) {
                logger.info("Dry run was selected, skipping save")
            } else {
                logger.info("Saving copied series episodes and movies")
                storageService.mergeShows(CopiedShows.createFrom(copyables))
            }
        }

        logger.info("Finished copy at ${Clock.System.now()}")
    }
}
