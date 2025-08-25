package eu.abelk.showcopier.modules

import eu.abelk.showcopier.DownloadCommand
import eu.abelk.showcopier.client.DownloadClient
import eu.abelk.showcopier.client.RadarrClient
import eu.abelk.showcopier.client.SonarrClient
import eu.abelk.showcopier.domain.cli.Parameters
import eu.abelk.showcopier.service.DownloadService
import eu.abelk.showcopier.service.MoviesService
import eu.abelk.showcopier.service.SeriesService
import eu.abelk.showcopier.service.StorageService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.hours

class Modules(parameters: Parameters) {
    private val container = Container()

    init {
        with(container) {
            register {
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            }
            register {
                HttpClient(CIO) {
                    install(Logging) {
                        sanitizeHeader { header -> header == HttpHeaders.Authorization }
                    }
                    install(ContentNegotiation) {
                        json(get<Json>())
                    }
                    install(HttpTimeout) {
                        requestTimeoutMillis = 5.hours.inWholeMilliseconds
                    }
                }
            }
            register {
                StorageService(
                    json = get<Json>(),
                    copiedShowsFile = parameters.storageFile
                )
            }
            register {
                SonarrClient(
                    httpClient = get<HttpClient>(),
                    sonarrBaseUrl = parameters.sonarrUrl.toString(),
                    sonarrApiKey = parameters.sonarrApiKey
                )
            }
            register {
                RadarrClient(
                    httpClient = get<HttpClient>(),
                    radarrBaseUrl = parameters.radarrUrl.toString(),
                    radarrApiKey = parameters.radarrApiKey
                )
            }
            register {
                SeriesService(
                    sonarrClient = get<SonarrClient>(),
                    storageService = get<StorageService>(),
                    tag = parameters.tag
                )
            }
            register {
                MoviesService(
                    radarrClient = get<RadarrClient>(),
                    storageService = get<StorageService>(),
                    tag = parameters.tag
                )
            }
            register {
                DownloadClient(
                    httpClient = get<HttpClient>(),
                    downloadUsername = parameters.downloadUsername,
                    downloadPassword = parameters.downloadPassword,
                    maxDownloadBufferSize = parameters.maxDownloadBufferSize
                )
            }
            register {
                DownloadService(
                    downloadClient = get<DownloadClient>(),
                    downloadBaseUrl = parameters.downloadBaseUrl.toString(),
                    downloadDirectory = parameters.destination,
                    pathPrefix = parameters.pathPrefix,
                    dryRun = parameters.dryRun
                )
            }
            register {
                DownloadCommand(
                    seriesService = get<SeriesService>(),
                    moviesService = get<MoviesService>(),
                    downloadService = get<DownloadService>(),
                    storageService = get<StorageService>(),
                    maxParallelDownloads = parameters.maxParallelDownloads,
                    dryRun = parameters.dryRun
                )
            }
        }
    }

    inline fun <reified T: Any> get() =
        get(T::class)

    fun <T: Any> get(type: KClass<T>) =
        container.get(type)
}