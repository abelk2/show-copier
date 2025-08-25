package eu.abelk.showcopier.client

import eu.abelk.showcopier.logging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.remaining
import io.ktor.utils.io.exhausted
import io.ktor.utils.io.readRemaining
import kotlinx.io.RawSink

class DownloadClient(
    private val httpClient: HttpClient,
    private val downloadUsername: String,
    private val downloadPassword: String,
    private val maxDownloadBufferSize: Long
) {
    private val logger = logger()

    suspend fun getFile(downloadUrl: String, sink: RawSink) =
        httpClient
            .prepareGet(downloadUrl) {
                basicAuth(downloadUsername, downloadPassword)
            }
            .execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                var count = 0L
                var lastLoggedPercent = 0
                sink.use {
                    while (!channel.exhausted()) {
                        val chunk = channel.readRemaining(max = maxDownloadBufferSize)
                        count += chunk.remaining
                        chunk.transferTo(sink)

                        val percent = httpResponse.contentLength()?.let { ((count.toDouble() / it) * 100).toInt() }
                        if (percent != null && percent - lastLoggedPercent >= 20) {
                            logger.info("[$percent%] $downloadUrl")
                            lastLoggedPercent = percent
                        }
                    }
                }
            }
}
