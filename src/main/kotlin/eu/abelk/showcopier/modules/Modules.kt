package eu.abelk.showcopier.modules

import eu.abelk.showcopier.CopyCommand
import eu.abelk.showcopier.client.RadarrClient
import eu.abelk.showcopier.client.SonarrClient
import eu.abelk.showcopier.domain.cli.Parameters
import eu.abelk.showcopier.service.CopyService
import eu.abelk.showcopier.service.MoviesService
import eu.abelk.showcopier.service.SeriesService
import eu.abelk.showcopier.service.StorageService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

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
                    install(Logging)
                    install(ContentNegotiation) {
                        json(get<Json>())
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
                CopyService(
                    destination = parameters.destination,
                    pathPrefix = parameters.pathPrefix,
                    dryRun = parameters.dryRun
                )
            }
            register {
                CopyCommand(
                    seriesService = get<SeriesService>(),
                    moviesService = get<MoviesService>(),
                    copyService = get<CopyService>(),
                    storageService = get<StorageService>(),
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