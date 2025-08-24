package eu.abelk.showcopier.client

import io.ktor.client.HttpClient
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel

class DownloadClient(
    private val httpClient: HttpClient,
    private val downloadUsername: String,
    private val downloadPassword: String
) {
    suspend fun getFile(downloadUrl: String): ByteReadChannel =
        httpClient
            .get(downloadUrl) {
                basicAuth(downloadUsername, downloadPassword)
            }
            .bodyAsChannel()
}
