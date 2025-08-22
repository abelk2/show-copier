package eu.abelk.showcopier.client

import eu.abelk.showcopier.domain.sonarr.SonarrEpisode
import eu.abelk.showcopier.domain.sonarr.SonarrEpisodeFile
import eu.abelk.showcopier.domain.sonarr.SonarrSeries
import eu.abelk.showcopier.domain.sonarr.SonarrTag
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SonarrClient(
    private val httpClient: HttpClient,
    private val sonarrBaseUrl: String,
    private val sonarrApiKey: String
) {
    suspend fun getSeries(): Set<SonarrSeries> =
        httpClient
            .get("$sonarrBaseUrl/series") {
                commonHeaders()
            }
            .body()

    suspend fun getEpisodes(seriesId: Int, seasonNumber: Int): Set<SonarrEpisode> =
        httpClient
            .get("$sonarrBaseUrl/episode") {
                commonHeaders()
                url {
                    parameter("seriesId", seriesId)
                    parameter("seasonNumber", seasonNumber)
                    parameter("includeEpisodeFile", true)
                }
            }
            .body()

    suspend fun getEpisodeFile(episodeFileId: Int): SonarrEpisodeFile =
        httpClient
            .get("$sonarrBaseUrl/episodefile/$episodeFileId") {
                commonHeaders()
            }
            .body()

    suspend fun getTags(): Set<SonarrTag> =
        httpClient
            .get("$sonarrBaseUrl/tag") {
                commonHeaders()
            }
            .body()

    private fun HttpRequestBuilder.commonHeaders() {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        headers {
            append("X-Api-Key", sonarrApiKey)
        }
    }
}