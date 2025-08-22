package eu.abelk.showcopier.client

import eu.abelk.showcopier.domain.radarr.RadarrMovie
import eu.abelk.showcopier.domain.radarr.RadarrTag
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RadarrClient(
    private val httpClient: HttpClient,
    private val radarrBaseUrl: String,
    private val radarrApiKey: String
) {
    suspend fun getMovies(): Set<RadarrMovie> =
        httpClient
            .get("$radarrBaseUrl/movie") {
                commonHeaders()
            }
            .body()

    suspend fun getTags(): Set<RadarrTag> =
        httpClient
            .get("$radarrBaseUrl/tag") {
                commonHeaders()
            }
            .body()

    private fun HttpRequestBuilder.commonHeaders() {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        headers {
            append("X-Api-Key", radarrApiKey)
        }
    }
}
