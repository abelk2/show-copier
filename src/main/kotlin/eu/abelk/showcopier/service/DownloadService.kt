package eu.abelk.showcopier.service

import eu.abelk.showcopier.client.DownloadClient
import eu.abelk.showcopier.logging.logger
import io.ktor.http.encodeURLPath
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyTo
import java.io.File

class DownloadService(
    private val downloadClient: DownloadClient,
    private val downloadBaseUrl: String,
    private val downloadDirectory: String,
    private val pathPrefix: String,
    private val dryRun: Boolean
) {
    private val logger = logger()

    suspend fun downloadFile(originalPath: String) {
        val relativePath = originalPath
            .removePrefix(pathPrefix)
            .stripLeadingSlash()
        val downloadUrl = "${downloadBaseUrl.stripTrailingSlash()}/${relativePath.encodeURLPath()}"
        val downloadPath = "${downloadDirectory.stripTrailingSlash()}/$relativePath"
        if (dryRun) {
            logger.info("Would download: $downloadUrl -> $downloadPath")
        } else {
            logger.info("Downloading: $downloadUrl -> $downloadPath")
            downloadClient.getFile(downloadUrl)
                .copyTo(File(downloadPath).writeChannel())
            logger.info("Downloaded: $downloadUrl")
        }
    }

    fun String.stripLeadingSlash() = removePrefix("/")

    fun String.stripTrailingSlash() = removeSuffix("/")
}