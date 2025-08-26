package eu.abelk.showcopier

import eu.abelk.showcopier.domain.storage.CopiedShows
import eu.abelk.showcopier.logging.logger
import eu.abelk.showcopier.service.DownloadService
import eu.abelk.showcopier.service.MoviesService
import eu.abelk.showcopier.service.SeriesService
import eu.abelk.showcopier.service.StorageService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DownloadCommand(
    private val seriesService: SeriesService,
    private val moviesService: MoviesService,
    private val downloadService: DownloadService,
    private val storageService: StorageService,
    private val maxParallelDownloads: Int,
    private val dryRun: Boolean
) {
    private val logger = logger()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun run() {
        logger.info("Starting downloads at ${Clock.System.now()}")

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
            logger.info("Nothing to download")
        } else {
            logger.info("Downloading ${copyables.size} series episodes and movies")
            coroutineScope {
                copyables.asFlow()
                    .flatMapMerge(maxParallelDownloads) { copyable ->
                        flow {
                            downloadService.downloadFile(copyable.path)
                            if (!dryRun) {
                                logger.info("Saving downloaded show state: $copyable")
                                storageService.mergeShows(CopiedShows.createFrom(copyable))
                            }
                            emit(Unit)
                        }
                    }.collect()
            }
        }

        logger.info("Finished downloads at ${Clock.System.now()}")
    }
}
