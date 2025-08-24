package eu.abelk.showcopier

import dev.inmo.krontab.doInfinity
import eu.abelk.showcopier.domain.cli.Parameters
import eu.abelk.showcopier.logging.logger
import eu.abelk.showcopier.modules.Modules
import picocli.CommandLine
import picocli.CommandLine.PicocliException

private val logger = logger("Main")

suspend fun main(args: Array<String>) {
    val parameters = Parameters()
    try {
        val parseResult = CommandLine(parameters).parseArgs(*args)
        if (!CommandLine.printHelpIfRequested(parseResult)) {
            logger.info("Starting show copier with arguments: $parameters")

            if (parameters.schedule.isNullOrBlank()) {
                logger.info("No schedule requested, running only once")
                runDownloads(parameters)
            } else {
                logger.info("Schedule requested, scheduling: ${parameters.schedule}")
                doInfinity(parameters.schedule!!) {
                    runDownloads(parameters)
                }
            }
        }
    } catch (exception: PicocliException) {
        System.err.println(exception.message)
    }
}

private suspend fun runDownloads(parameters: Parameters) =
    runCatching {
        Modules(parameters)
            .get<DownloadCommand>()
            .run()
    }.onFailure { throwable ->
        logger.error("Failed to run downloads", throwable)
    }
