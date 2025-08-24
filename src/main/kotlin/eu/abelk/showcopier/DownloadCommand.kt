package eu.abelk.showcopier

import eu.abelk.showcopier.domain.storage.CopiedShows
import eu.abelk.showcopier.logging.logger
import eu.abelk.showcopier.service.DownloadService
import eu.abelk.showcopier.service.MoviesService
import eu.abelk.showcopier.service.SeriesService
import eu.abelk.showcopier.service.StorageService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
                copyables.chunked(maxParallelDownloads)
                    .forEach { chunk ->
                        chunk.map { copyable ->
                            async {
                                downloadService.downloadFile(copyable.path)
                            }
                        }
                        .awaitAll()
                    }
            }

            if (dryRun) {
                logger.info("Dry run was selected, skipping save")
            } else {
                logger.info("Saving list of downloaded series episodes and movies")
                storageService.mergeShows(CopiedShows.createFrom(copyables))
            }
        }

        logger.info("Finished downloads at ${Clock.System.now()}")
    }
}
